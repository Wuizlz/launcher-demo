# Launcher Demo

## What the code is

This project is a simple Java menu-driven launcher that runs common Windows utilities from a single terminal prompt. The core logic lives in `launcher.java`, which:

- Builds a numbered menu of options.
- Reads and validates user input.
- Uses `ProcessBuilder` to start a selected executable from `C:\Windows\System32`.
- For options that spawn terminal-based programs (NSLookup and CMD), it inherits the parent console’s I/O and waits for the child process to exit before returning to the menu.

The `launcher_demo.cmd` script is a convenience wrapper that starts the packaged `Launcher.jar`.

## What it prompts and which commands it runs

When you run the jar (or the `launcher_demo.cmd` script), the program shows a menu and asks for a numeric selection. Each non-zero entry maps to a specific Windows executable:

| Menu option | Executable | Description |
| --- | --- | --- |
| 1 | `Taskmgr.exe` | Task Manager |
| 2 | `notepad.exe` | Notepad |
| 3 | `charmap.exe` | Character Map |
| 4 | `Sndvol.exe` | Sound Volume Mixer |
| 5 | `winver.exe` | About Windows |
| 6 | `msinfo32.exe` | System Information |
| 7 | `nslookup.exe` | NSLookup (console) |
| 8 | `cmd.exe` | Command Prompt (console) |
| 0 | (exit) | Quit the launcher |

Notes:

- Options 7 and 8 are console programs. The launcher inherits the terminal I/O so you can interact with them directly. Once they exit, the launcher prints their CPU time and returns to the menu.
- All executables are launched from `C:\Windows\System32`.

## Process tree / activity monitor notes (for screenshots)

<img width="395" height="251" alt="Screenshot 2026-01-26 at 10 20 02 PM" src="https://github.com/user-attachments/assets/cd8b2138-7c98-4b04-9a8e-78bbc6217ae0" />
##Screenshot 1 (Option 2 → Notepad)
In this screenshot there are two separate Windows Terminal sessions running the launcher.
	•	Session A: explorer.exe → WindowsTerminal.exe → OpenConsole.exe → powershell.exe
I cd to Desktop\CS33600\launcher-demo and run .\launcher_demo.cmd, which starts the jar (java.exe).
Selecting option 2 launches Notepad, so you see:
cmd/powershell → java.exe → Notepad.exe.
	•	Session B: Same idea, but in a different folder:
cd to Downloads\hw1 and run .\launcher_demo.cmd again, creating another shell → java.exe chain.

So the repeated OpenConsole / powershell / cmd / java entries are just two independent terminal sessions, and Notepad appears as a child of the specific java.exe that launched it.

Explorer ➜ Windows Terminal ➜ OpenConsole ➜ PowerShell/CMD ➜ java.exe ➜ Notepad.exe. Two separate terminal sessions are running the launcher from different directories.

<img width="422" height="291" alt="image" src="https://github.com/user-attachments/assets/157bcc24-e234-49e3-ac69-db72649b8154" />

##Screenshot 2 (Option 8 → spawns a new CMD → runs jar again)
This screenshot starts the same way (Terminal session(s) running .\launcher_demo.cmd), but I select option 8, which launches another cmd.exe from inside the launcher.

That’s why you now see an extra nested chain:
	•	Main launcher path: … → powershell/cmd → java.exe
	•	Option 8 creates: java.exe → cmd.exe
	•	Inside that new CMD I run the launcher again, creating:
cmd.exe → java.exe (a second Java process under the new CMD)

So the “extra” cmd.exe → java.exe you see is expected: option 8 spawns a new command prompt, and running the script again in that prompt spawns a second Java process.

Explorer ➜ Terminal ➜ OpenConsole ➜ PowerShell/CMD ➜ java.exe, then option 8 spawns a new cmd.exe, and running the script again creates an additional cmd.exe ➜ java.exe chain.


### Why siblings can appear

“Siblings” can appear because the viewer is not always showing a pure “who spawned who” tree — it may be showing a logical grouping of processes that cooperate closely.

Here are the main reasons (in plain terms):
	•	Job objects + IPC grouping (logical grouping):
Windows can put related processes into the same Job object or tightly connect them via inter-process communication (IPC). Some tools then group them side-by-side because they “belong together,” even if one technically started the other.
	•	Modern terminal architecture (broker/host split):
With Windows Terminal, the “terminal” experience is split across multiple processes:
	•	WindowsTerminal.exe = the UI
	•	OpenConsole.exe / conhost.exe = console host plumbing (ConPTY)
	•	cmd.exe / powershell.exe = the shell
Depending on timing and how ConPTY is wired, a viewer may show cmd.exe and OpenConsole.exe as “siblings” under the same parent (or under a broker process) even though the actual startup chain still exists.
	•	Reparenting / intermediate launchers:
Sometimes a process is launched by a helper (a broker) and ends up logically associated with another process. The strict parent PID might point to the helper, while the UI groups it under the app you think launched it.

Even if the UI makes two processes look like siblings, it doesn’t mean they’re unrelated — it usually means they’re part of the same job/terminal session and the tool is prioritizing a logical view over strict parent/child spawning.

## Running the demo

https://github.com/user-attachments/assets/a3a8fa79-0576-4aa6-bd14-9bfb88b468b2


From a command prompt in this directory:

```cmd
launcher_demo.cmd
```

Or directly:

```cmd
java -jar Launcher.jar
```
