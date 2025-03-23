package si.janez.dtos.match;

import si.janez.exceptions.ApplicationException;

public class MatchResultDto {
    private String matchId;
    public Long marketId;
    public String outcomeId;
    public String specifiers;

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchIdString() {
        return matchId;
    }

    public long getMatchId() {
        return parseMatchId(matchId);
    }

    // Example id sr:match:13762991
    public static long parseMatchId(String matchId) {
        var parts = matchId.split(":");
        var prefix = switch (parts[1]) {
            case "match" -> "1";
            case "simple_tournament" -> "2";
            case "stage" -> "3";
            case "season" -> "4";
            default -> throw new ApplicationException("Invalid match id: " + matchId);
        };
        return Long.parseLong(prefix + parts[2]);
    }
}