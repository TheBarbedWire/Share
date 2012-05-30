
public class ShareSetup
{
    public String configPath;
    public int PLport;
    public int SPport;
    public int PCport;
    public int CLport;
    public int FTport;
    public int SFTport;
   
    public ShareSetup(String configPath, int PLport, int SPport, int PCport, int CLport, int FTport, int SFTport)
    {
        this.configPath = configPath;
        this.PLport = PLport;
        this.SPport = SPport;
        this.PCport = PCport;
        this.CLport = CLport;
        this.FTport = FTport;
        this.SFTport = SFTport;
    }

}
