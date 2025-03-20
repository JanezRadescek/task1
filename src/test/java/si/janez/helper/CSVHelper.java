package si.janez.helper;

import io.quarkus.logging.Log;
import si.janez.dtos.match.MatchResultDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Example csv
//MATCH_ID|MARKET_ID|OUTCOME_ID|SPECIFIERS
//'sr:match:13762991'|60|'2'|
public class CSVHelper {

    static Set<String> matchIdsPrefixes = new HashSet<String>();
    public static List<MatchResultDto> loadCsvFromResource(String resourcePath) throws IOException {
        List<MatchResultDto> results = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(
                CSVHelper.class.getResourceAsStream(resourcePath));
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            // Skip header line
            String line = bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\|", 4); // -1 to keep empty trailing fields

                if (parts.length >= 4) {
                    MatchResultDto dto = new MatchResultDto();

                    dto.setMatchId(parts[0].replaceAll("'", "").trim());
                    analyzeMatchId(dto.getMatchIdString());
                    dto.marketId = Long.parseLong(parts[1].trim());
                    dto.outcomeId = parts[2].replaceAll("'", "").trim();
                    dto.specifiers = parts[3].replaceAll("'", "").trim();

                    results.add(dto);
                } else {
                    throw new IOException("Invalid CSV line: " + line);
                }
            }
        }

        Log.info("Prefixes: " + matchIdsPrefixes);

        return results;
    }

    private static void analyzeMatchId(String matchIdString) throws IOException {
        var parts = matchIdString.split(":");
        if(parts.length == 3){
            if(!parts[0].equals("sr"))
                throw new IOException("Invalid matchId: " + matchIdString);
            matchIdsPrefixes.add(parts[1]);

            try {
                Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid matchId number format: " + matchIdString, e);
            }
        } else {
            throw new IOException("Invalid matchId: " + matchIdString);
        }
    }
}
