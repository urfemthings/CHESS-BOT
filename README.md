use 6 concepts
-IPC (Inter-Process Communication)
-Multi-Threading & Asynchronous Programming
-State Management & Protocol Parsing
-OOP (Object-Oriented Programming)
-Cross-Platform / Cross-Runtime Integration
-Client-Server Architecture (Local Host)

so the Java is FRONTEND
and the c#/bot is BACKEND

Java (Swing) = Frontend (Client)
C# + Stockfish = Backend (Server/Engine)

so the program communication using protokol (in Bridge.cs and Board.java)
in Java swing (Board.java )as gui front end communication  with c#/bot(Bridge.cs) code stocfish using consep IPC (Inter-Process Communication)."

proff video 
gameplay:
https://github.com/urfemthings/CHESS-BOT/raw/main/Screenrecording_20260826_195310.mp4
lowkey i try 2000 elo (can custom up to 3500+ elo)and i lose but here the results 

Tools i used: nano (Text Editor), OpenJDK (Java 21), dan Mono (C#), vim,termux,x11

how to run it;

download termux x-11 
open termux-x11 
(run in background)
open temrux 
type this command : termux-x11 :0 -xstartup "xfce4-session" &
in 1 section

now section add section 2
run this cd ~/'catur + bot'/catur2lol_csharp && mcs *.cs -out:Bridge.exe && cd ~/'catur + bot'/catur2lol && rm -f *.class && javac *.java && export DISPLAY=:0 && java MainBoard2

open x11 again and wait for a little bit ,when it show there you go ,nice and have fun to play my chess 👍