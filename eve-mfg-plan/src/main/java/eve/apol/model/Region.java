package eve.apol.model;

public enum Region {
    SinqLaison(10000032L), Metropolis(10000042L), TheForge(10000002L), Heimatar(10000030L), MoldenHeath(10000028L), Domain(10000043L);

    private long regionID;

    private Region(long regionId) {
        this.regionID = regionId;
    }

    public long getRegionID() {
        return regionID;
    }
}
