package data;

/**
 * Created by NweYiAung on 04-02-2017.
 */
public class RankingTypeData {

    public String rankingTypeName;
    public int rankingTypeID;

    public int getRankingTypeID(){
        return rankingTypeID;
    }

    public void setRankingTypeID(int rankingTypeID){
        this.rankingTypeID=rankingTypeID;
    }

    public String getRankingTypeName(){
        return rankingTypeName;
    }

    public void setRankingTypeName(String rankingTypeName){
        this.rankingTypeName=rankingTypeName;
    }
}
