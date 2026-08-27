using System;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;

class Program
{
    static Process stockfish;
    static StreamWriter sfWriter;
    static StreamReader sfReader;
    static List<string> moveHistory = new List<string>();

    static void Main(string[] args)
    {
        ProcessBuilder();

        string line;
        while ((line = Console.ReadLine()) != null)
        {
            if (line.StartsWith("MOVE:"))
            {
                string[] p = line.Substring(5).Split(',');
                int sCol = int.Parse(p[0]);
                int sRow = int.Parse(p[1]);
                int tCol = int.Parse(p[2]);
                int tRow = int.Parse(p[3]);

                string uciMove = ConvertToUCI(sCol, sRow, tCol, tRow);
                moveHistory.Add(uciMove);
            }
            else if (line == "THINK")
            {
                string posCmd = "position startpos moves " + string.Join(" ", moveHistory);
                SendToStockfish(posCmd);
                
                SendToStockfish("go depth 10");

                string bestMoveUCI = ReadBestMove();

                if (!string.IsNullOrEmpty(bestMoveUCI))
                {
                    moveHistory.Add(bestMoveUCI);
                    int[] coords = ConvertFromUCI(bestMoveUCI);
                    
                    Console.WriteLine(coords[0] + "," + coords[1] + "," + coords[2] + "," + coords[3]);
                    Console.Out.Flush();
                }
            }
            else if (line == "QUIT")
            {
                SendToStockfish("quit");
                break;
            }
        }
    }

    static void ProcessBuilder()
    {
        stockfish = new Process();
        stockfish.StartInfo.FileName = "/data/data/com.termux/files/usr/bin/stockfish";
        stockfish.StartInfo.UseShellExecute = false;
        stockfish.StartInfo.RedirectStandardInput = true;
        stockfish.StartInfo.RedirectStandardOutput = true;
        stockfish.Start();

        sfWriter = stockfish.StandardInput;
        sfReader = stockfish.StandardOutput;

        SendToStockfish("uci");
        SendToStockfish("setoption name UCI_LimitStrength value true");
        SendToStockfish("setoption name UCI_Elo value 2000");
        SendToStockfish("isready");
    }

    static void SendToStockfish(string cmd)
    {
        sfWriter.WriteLine(cmd);
        sfWriter.Flush();
    }

    static string ReadBestMove()
    {
        string line;
        while ((line = sfReader.ReadLine()) != null)
        {
            if (line.StartsWith("bestmove"))
            {
                string[] parts = line.Split(' ');
                return parts[1];
            }
        }
        return null;
    }

    static string ConvertToUCI(int sCol, int sRow, int tCol, int tRow)
    {
        char sF = (char)('a' + sCol);
        int sR = 8 - sRow;
        char tF = (char)('a' + tCol);
        int tR = 8 - tRow;

        return $"{sF}{sR}{tF}{tR}";
    }

    static int[] ConvertFromUCI(string uci)
    {
        int sCol = uci[0] - 'a';
        int sRow = 8 - (uci[1] - '0');
        int tCol = uci[2] - 'a';
        int tRow = 8 - (uci[3] - '0');

        return new int[] { sCol, sRow, tCol, tRow };
    }
}
