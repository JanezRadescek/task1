package si.janez.dtos.match;

public class MatchResultDto {

    private final static String prefix = "sr:match:";
    private final static int prefixLength = prefix.length();

    private String matchId;
    public Long marketId;
    public Long outcomeId;
    public String specifiers;

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchIdString(){
        return matchId;
    }

    public long getMatchId() {
        return parseMatchId(matchId);
    }

    public static long parseMatchId(String matchId) {
        return Long.parseLong(matchId.substring(prefixLength));
    }
}