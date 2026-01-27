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


### Why siblings can appear

Some process viewers label certain related processes as “siblings” even though they have a strict parent/child chain. This can happen due to:

- **Job objects and IPC grouping**: tools may group related processes together to show logical relationships, not just strict parentage.
- **Terminal host models**: in modern Windows terminals, the console host and the shell can be in separate processes, and the UI can make them look like siblings even if one is technically parented by a broker process.

A clear, simple way to describe it in your screenshot captions is:

> “Explorer is the top-level parent because it launched the terminal. The terminal hosts `cmd.exe`, and `cmd.exe` starts the Java launcher (and any utilities it opens). Some viewers group these as siblings for UI/IPC reasons, even though the start order still follows this parent-child chain.”

## Screenshot placeholder (Activity Monitor / Task Manager)

Place your process tree screenshots in this section. When you add the images, the recommended caption format is:

> “Explorer ➜ Terminal ➜ cmd.exe ➜ java.exe ➜ launcher/utility. Explorer is the original parent because it launched the terminal; some viewers show these as siblings for IPC or job-grouping reasons, but the launch order still follows this chain.”

If you want to call out the “siblings” wording explicitly, you can add:

> “Even when the viewer groups processes as siblings, it’s still accurate to explain Explorer as the top-level parent and the terminal/cmd/utility as the child chain.”

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
