import java.net.*;

public class Main
{
   

    public static void Main()
    {      
        String configPath = "./";
        int PLport = 1234;
        int SPport = 1238;
        int PCport = 1235;
        int CLport = 1236;
        int FTport = 1237;
        int SFTport = 1239;
        try {
            ShareSetup setup = new ShareSetup(configPath, PLport, SPport, PCport, CLport, FTport, SFTport);
            GUI gui = new GUI(setup);
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }
    
    public static void Main(String[] args)
    {
        String configPath = "./";
        int PLport = 1234;
        int SPport = 1238;
        int PCport = 1235;
        int CLport = 1236;
        int FTport = 1237;
        int SFTport = 1239;
        try {
            ShareSetup setup = new ShareSetup(configPath, PLport, SPport, PCport, CLport, FTport, SFTport);
            CLI cli = new CLI(setup);
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }


}
