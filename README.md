# Launcher Demo

A small Java menu-driven launcher that runs common Windows utilities from a single terminal prompt. The core logic is implemented in `launcher.java`. A convenience wrapper script `launcher_demo.cmd` starts the packaged `Launcher.jar`.

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

Description:
- Two separate Windows Terminal sessions run the launcher.
  - Session A: explorer.exe → WindowsTerminal.exe → OpenConsole.exe → powershell.exe → (you run) `launcher_demo.cmd` → `java.exe` → `Notepad.exe`
  - Session B: same sequence from a different working directory, producing another `java.exe` → `Notepad.exe`
- The repeated terminal/console processes are independent terminal sessions; each Notepad appears as a child of the specific `java.exe` that launched it.

Screenshot 2 — Option 8 (Spawn CMD and run the jar again)
![Screenshot 2 (Option 8 → new CMD → run jar again)](https://github.com/user-attachments/assets/157bcc24-e234-49e3-ac69-db72649b8154)

Description:
- Selecting option 8 spawns a new `cmd.exe` from the launcher (`java.exe` → `cmd.exe`).
- If you run the launcher again inside that new CMD, you will see another `cmd.exe` → `java.exe` chain nested under the spawned prompt. This is expected.

Why siblings can appear
- Process viewers sometimes show logical groupings rather than a strict spawn (parent PID) tree.
- Common reasons:
  - Job objects and IPC grouping: Windows may group related processes in job objects, and viewers can display those related processes side-by-side.
  - Terminal broker/host architecture: Windows Terminal splits responsibilities (UI vs. console host). The broker and console host processes (WindowsTerminal.exe, OpenConsole.exe/conhost.exe) can cause viewer GUIs to present processes in non-linear groupings.
  - Reparenting / intermediate launchers: Helpers or brokers may be the strict parent PID while the UI groups the child under a different visible node.
- In short: "siblings" in a viewer often reflect logical/session grouping rather than a strict parent-child relationship.

## Running the demo



https://github.com/user-attachments/assets/1e18ec07-a6ef-41cd-be6b-6f0843dbbfe9



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
