# Launcher Demo

A small Java menu-driven launcher that runs common Windows utilities from a single terminal prompt. The core logic is implemented in `launcher.java`. A convenience wrapper script `launcher_demo.cmd` starts the jar for you.

## Overview

The launcher:

- Builds a numbered menu of options.
- Reads and validates numeric user input.
- Uses `ProcessBuilder` to start a selected executable from `C:\Windows\System32`.
- For console-based options (NSLookup and CMD), it inherits the parent console I/O and waits for the child process to exit before returning to the menu.

## Menu mapping

When you run the jar or the `launcher_demo.cmd` script, the program shows a menu and asks for a numeric selection. Each non-zero entry maps to a specific Windows executable:

| Menu option | Executable      | Description                     |
|-------------|------------------|---------------------------------|
| 1           | `Taskmgr.exe`    | Task Manager                    |
| 2           | `notepad.exe`    | Notepad                         |
| 3           | `charmap.exe`    | Character Map                   |
| 4           | `Sndvol.exe`     | Sound Volume Mixer              |
| 5           | `winver.exe`     | About Windows                   |
| 6           | `msinfo32.exe`   | System Information              |
| 7           | `nslookup.exe`   | NSLookup (console)              |
| 8           | `cmd.exe`        | Command Prompt (console)        |
| 0           | (exit)           | Quit the launcher               |

Notes:
- Options 7 and 8 are console programs: the launcher inherits the terminal I/O so you can interact directly. The launcher prints the child process CPU time after they exit and then returns to the menu.
- All executables are launched from `C:\Windows\System32`.

## Screenshots / Process notes

Screenshot 1 — Option 2 (Notepad)  
![Screenshot 1 (Option 2 → Notepad)](https://github.com/user-attachments/assets/cd8b2138-7c98-4b04-9a8e-78bbc6217ae0)

Clear parent/child chain (what launched what):
- explorer.exe → WindowsTerminal.exe → OpenConsole.exe (or conhost.exe) → powershell.exe → `launcher_demo.cmd` → `java.exe` → `Notepad.exe`.
- Here, `java.exe` is the direct parent of `Notepad.exe` (Notepad was spawned by the Java launcher).
- Everything earlier in the chain is the ancestry that led to the terminal owner and shell that in turn started the launcher.

Notes about how this appears in viewers:
- In a strict parent-PID tree, Notepad is a child of `java.exe`. In many GUI process viewers, however, you may see Notepad visually appear grouped under the terminal host or alongside other apps. That visual grouping reflects session/console relationships, not an incorrect parent-child event — the process was still created by `java.exe`.

Screenshot 2 — Option 8 (Spawn CMD and run the jar again)  
![Screenshot 2 (Option 8 → new CMD → run jar again)](https://github.com/user-attachments/assets/157bcc24-e234-49e3-ac69-db72649b8154)

Clear parent/child chain (what launched what):
- When you select option 8, the launcher (`java.exe`) creates `cmd.exe`. So the chain is: `java.exe` → `cmd.exe`.
- If you run the launcher again from that new `cmd.exe`, the newly started Java process will be a child of the spawned `cmd.exe`: `java.exe` (original) → `cmd.exe` → `java.exe` (new) → (child, e.g., `Notepad.exe` if launched from the inner jar).
- In this nested scenario, the immediate parent of the new `java.exe` instance is the `cmd.exe` you spawned from the original `java.exe`.

## Process relationships, sibling grouping, and IPC (Notes)

This project spawns processes to demonstrate launcher behaviour, so it helps to understand how parent/child relationships, "sibling" grouping in viewers, reparenting, and IPC work in practice.

### Parent / child lineage vs. what you see in UI tools
- The kernel (or OS) records which process called CreateProcess/fork+exec, and that recorded Parent PID (PPID) is the authoritative creation lineage — who launched what.
- Many GUI process viewers (Task Manager, Windows Terminal UI, Process Hacker in some views) show logical grouping — by session, console host, job object, or terminal renderer — which can make processes that actually have a parent-child relationship appear as siblings (side-by-side) or grouped under a different UI node.
  - Example: Notepad might have been created by a Java launcher (so the kernel PPID shows `java.exe` → `notepad.exe`) but a GUI viewer may visually place Notepad under the console host or alongside other apps because it groups by session or console, not by raw PPID.
- When you need the strict creation lineage, inspect the PPID:
  - Windows: Process Explorer (enable tree view), `Get-CimInstance Win32_Process | select ProcessId, ParentProcessId, Name`
  - PowerShell: `Get-Process -IncludeUserName` and check `ParentProcessId` (or use WMIC/CIM)
  - Linux/Unix: `ps -o pid,ppid,cmd -p <pid>` or `pstree -p`
- Summary: UI grouping = logical/session view. PPID = who actually invoked the process creation.

### Reparenting / orphan adoption
- Unix-like systems (Linux, macOS): if a parent process exits before its children, the child is reparented — typically to the init/systemd process (PID 1) or to a configured subreaper. The child continues running and is adopted by that reaper.
  - You can influence this with subreapers (prctl PR_SET_CHILD_SUBREAPER on Linux).
- Windows: the kernel does not perform the same "adopt by PID 1" reparenting. The PPID recorded at process creation remains the PPID value even if the parent exits; some tools may display the child under a different grouping because of session/console/job membership or because a helper (console host) is involved. Windows also has job objects which can tie lifecycle: if a job object is configured, children assigned to it can be terminated together.
- Practical effect for launcher/demo:
  - In a nested example (Option 8 in this repo): the original launcher runs `cmd.exe`, forming `java.exe` → `cmd.exe`. If you run the launcher again from that spawned `cmd.exe`, the newly started Java process is a child of that `cmd.exe`: `java.exe (original) → cmd.exe → java.exe (new)`. A Notepad launched from the inner jar will be a child of that inner `java.exe`.
  - When you see "siblings" in the viewer, that's usually the viewer choosing to show all processes in the same session or console together, not an indication the kernel created them as siblings.

### Why a crashed process usually doesn't take down unrelated siblings
- Separation of concerns: each process has its own memory space, file descriptors (unless explicitly shared), and threads. A crash in one process will generally not corrupt another process's memory or threads.
- Exceptions where one process can affect others:
  - Shared kernel resources (drivers, kernel-mode components) or buggy shared libraries used in-process.
  - Explicitly shared memory, file locks, named pipes, or sockets — poorly designed protocols can allow one party to disrupt others.
  - Job objects in Windows: processes in the same job can be terminated together or inherit limits that cause collective failures.
  - Parent-side supervision strategies (e.g., the parent intentionally terminating children on fatal failure).
- Design guidance to keep failures isolated:
  - Use processes for strong isolation (separate concerns into separate processes).
  - Prefer message-passing or well-defined IPC with timeouts and retries instead of tight in-memory coupling.
  - Use a supervisor/watchdog to restart failed components rather than having one process block others.
  - Avoid global/shared mutable state between processes where possible; if you must share state, use robust synchronization and failure-tolerant stores (databases, durable queues).

### IPC patterns and resilience
Common IPC mechanisms:
- Std streams / anonymous pipes — simple parent↔child command/response flows
- Named pipes (Windows/Unix FIFO) — local RPC and stream transport
- Sockets (TCP/Unix domain sockets) — network-transparent or local
- Shared memory + synchronization primitives — high-performance but requires careful correctness
- Message brokers / queues (Redis, RabbitMQ, Kafka) — decoupled, persistent, supports retries
- RPC (gRPC, Thrift, HTTP/REST) — structured APIs with deadlines and retries
- OS-specific mechanisms (WM_COPYDATA on Windows, signals on Unix for notifications)

Design for robustness:
- Use timeouts and backoffs for requests.
- Make operations idempotent when possible.
- Use a message queue or broker for decoupling and delivery guarantees.
- Make restarts and reconnections automatic and observable (health checks, metrics, logs).
- Ensure components fail closed (don’t leave shared resources in inconsistent state) and use explicit cleanup/lease patterns.

### Practical tips for verification and debugging
- Windows:
  - Process Explorer (Sysinternals) with Tree view shows PPID-based tree.
  - PowerShell: `Get-CimInstance Win32_Process | select ProcessId, ParentProcessId, Name, CommandLine`
  - `tasklist /v` or Process Hacker for additional views.
- Linux/macOS:
  - `pstree -p`, `ps -o pid,ppid,cmd`, `systemd-cgls` (for cgroups)
- When a process “looks” grouped under a console or terminal in a UI, cross-check PPIDs and start times to confirm the real creator.

---

In short:
- UI process grouping often reflects sessions, consoles, or job objects — not necessarily the kernel parent-child creation event.
- Kernel PPID (or the Unix reparenting to init/subreaper) is the authoritative lineage.
- Processes are a strong isolation boundary; design IPC intentionally so a failure in one process does not cascade to others and use supervisors to restart or contain faults.
## Running the demo

https://github.com/user-attachments/assets/60a39cda-96b1-4307-aa21-4b0a41178481

From a command prompt in this directory:

```cmd
launcher_demo.cmd
```

Or directly:

```cmd
java -jar Launcher.jar
```

## Files of interest

- `launcher.java` — core launcher logic (menu, input validation, ProcessBuilder usage)
- `launcher_demo.cmd` — convenience wrapper that runs `Launcher.jar`

## Notes / Troubleshooting

- The launcher assumes executables are present in `C:\Windows\System32`.
- Console programs (options 7 and 8) will return to the launcher after they exit; interactive work is possible while they run because I/O is inherited.
- If a program does not start, check file permissions, path, and whether the executable exists in `C:\Windows\System32`.
```
