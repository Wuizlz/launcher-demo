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

Why some processes show as siblings (side-by-side) instead of strict parent/child nodes

- Logical grouping vs. strict parent PID:
  - Many process viewers (including Task Manager and other UI tools) present processes grouped by user session, job object, or the console host, which can make processes that share the same console or session appear as siblings even if one actually launched the other.
  - The on-disk parent PID (PPID) still reflects which process performed the CreateProcess call, but UI grouping may hide that direct lineage.

- Terminal broker / console host abstraction:
  - Modern terminals (Windows Terminal) separate UI/renderer processes from the actual console host (OpenConsole.exe / conhost.exe). When a console process is created, the console host or terminal broker may be involved and can make the relationship in viewers look different from the raw PPID chain.
  - Example: a GUI terminal may show child shells and launched GUI apps under the terminal node even if the kernel PPID points to the immediate launcher process.

- Job objects and process groups:
  - Windows job objects can group processes for management (limits, termination together). Viewers that show job membership can display grouped processes as peers regardless of who created them.

- Reparenting and intermediate launchers:
  - Some helper processes (wrappers, service hosts, or elevated launchers) can be the real parent PID even though the UI shows a different hierarchical grouping. A process may be reparented by system components in some cases (for example if the original parent exits quickly).

How to verify the true parent-child relationships (practical tips)
- Use Process Explorer (Sysinternals) and enable the tree view: Process Explorer shows the PPID-based tree and will reveal the actual parent for each process.
- From PowerShell: run Get-Process -IncludeUserName and inspect the Id / ParentProcessId (or use Get-CimInstance Win32_Process | select ProcessId, ParentProcessId, Name).
- From cmd: use tasklist /v or third-party tools like Process Hacker to inspect parent PIDs and process start times.
- Checking PPIDs (parent PIDs) is the authoritative way to determine which process created another, while GUI groupings clarify logical/sessional associations.

In short: when you see "siblings" in a viewer, that usually reflects grouping by session, job object, or console host in the UI. The kernel-level parent (PPID) still indicates who actually invoked the process creation; use Process Explorer / Get-CimInstance / similar to inspect the PPID if you need the strict creation lineage.

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
