package sports.acs560.performance_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        String csvFilename = "sports_data.csv";
        String reportFilename = "analysis_report.txt";
        List<Team> teams = readDataFromCSV(csvFilename);
        if (teams.isEmpty()) {
            System.out.println("No data found. Exiting.");
            return;
        }
        Analysis analysis = Analysis.getInstance();
        analyzeData(teams, analysis);
        createReport(analysis, reportFilename);
        System.out.println("Analysis complete. Report saved to " + reportFilename);
    }

    public static List<Team> readDataFromCSV(String filename) {
        List<Team> teams = new ArrayList<>();
        File dataFile = new File(filename);
        try (Scanner fileScanner = new Scanner(dataFile)) {
            boolean isFirstLine = true; 
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                Team team = TeamFactory.createTeam(data);
                teams.add(team);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return teams;
    }

    public static void analyzeData(List<Team> teams, Analysis analysis) {
        int totalPoints = teams.stream().mapToInt(Team::getPoints).sum();
        double meanPoints = (double) totalPoints / teams.size();
        analysis.setMeanPoints(meanPoints);
        teams.sort(Comparator.comparingInt(Team::getPoints));
        int size = teams.size();
        double medianPoints = (size % 2 == 0)
                ? (teams.get(size / 2 - 1).getPoints() + teams.get(size / 2).getPoints()) / 2.0
                : teams.get(size / 2).getPoints();
        analysis.setMedianPoints(medianPoints);
        Team highestWinsTeam = teams.stream().max(Comparator.comparingInt(Team::getWins)).orElse(null);
        analysis.setMostWinsTeam(highestWinsTeam);
        Team highestLossesTeam = teams.stream().max(Comparator.comparingInt(Team::getLosses)).orElse(null);
        analysis.setMostLossesTeam(highestLossesTeam);
        Team highPointsTeam = teams.stream().max(Comparator.comparingInt(Team::getPoints)).orElse(null);
        analysis.setHighestPointsTeam(highPointsTeam);
        Team lowestPointsTeam = teams.stream().min(Comparator.comparingInt(Team::getPoints)).orElse(null);
        analysis.setLowestPointsTeam(lowestPointsTeam);
    }

    public static void createReport(Analysis analysis, String filename) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write("Mean Points: " + analysis.getMeanPoints() + "\n");
            fileWriter.write("Median Points: " + analysis.getMedianPoints() + "\n");

            Team highestWinsTeam = analysis.getMostWinsTeam();
            if (highestWinsTeam != null) {
                fileWriter.write("Most Wins Team: " + highestWinsTeam.getName() + " (" + highestWinsTeam.getWins() + " wins)\n");
            }

            Team highestLossesTeam = analysis.getMostLossesTeam();
            if (highestLossesTeam != null) {
                fileWriter.write("Most Losses Team: " + highestLossesTeam.getName() + " (" + highestLossesTeam.getLosses() + " losses)\n");
            }

            Team highPointsTeam = analysis.getHighestPointsTeam();
            if (highPointsTeam != null) {
                fileWriter.write("Highest Points Team: " + highPointsTeam.getName() + " (" + highPointsTeam.getPoints() + " points)\n");
            }

            Team lowPointsTeam = analysis.getLowestPointsTeam();
            if (lowPointsTeam != null) {
                fileWriter.write("Lowest Points Team: " + lowPointsTeam.getName() + " (" + lowPointsTeam.getPoints() + " points)\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }

    // Static inner class for Team
    public static class Team {
        private int year;
        private String name;
        private String league;
        private int wins;
        private int losses;
        private int points;

        public Team(int year, String name, String league, int wins, int losses, int points) {
            this.year = year;
            this.name = name;
            this.league = league;
            this.wins = wins;
            this.losses = losses;
            this.points = points;
        }

        public int getPoints() { return points; }
        public int getWins() { return wins; }
        public String getName() { return name; }
        public String getLeague() { return league; }
        public int getYear() { return year; }
        public int getLosses() { return losses; }
    }

    // Static inner class for TeamFactory
    public static class TeamFactory {
        public static Team createTeam(String[] data) {
            int year = Integer.parseInt(data[0].trim());
            String name = data[1].trim();
            String league = data[2].trim();
            int wins = Integer.parseInt(data[3].trim());
            int losses = Integer.parseInt(data[4].trim());
            int points = Integer.parseInt(data[5].trim());
            return new Team(year, name, league, wins, losses, points);
        }
    }

    // Singleton Analysis class
    public static class Analysis {
        private static Analysis instance;

        private double meanPoints;
        private double medianPoints;
        private Team mostWinsTeam;
        private Team mostLossesTeam;
        private Team highestPointsTeam;
        private Team lowestPointsTeam;

        private Analysis() {} // Private constructor

        public static Analysis getInstance() {
            if (instance == null) {
                instance = new Analysis();
            }
            return instance;
        }

        public double getMeanPoints() { return meanPoints; }
        public void setMeanPoints(double meanPoints) { this.meanPoints = meanPoints; }
        public double getMedianPoints() { return medianPoints; }
        public void setMedianPoints(double medianPoints) { this.medianPoints = medianPoints; }
        public Team getMostWinsTeam() { return mostWinsTeam; }
        public void setMostWinsTeam(Team mostWinsTeam) { this.mostWinsTeam = mostWinsTeam; }
        public Team getMostLossesTeam() { return mostLossesTeam; }
        public void setMostLossesTeam(Team mostLossesTeam) { this.mostLossesTeam = mostLossesTeam; }
        public Team getHighestPointsTeam() { return highestPointsTeam; }
        public void setHighestPointsTeam(Team highestPointsTeam) { this.highestPointsTeam = highestPointsTeam; }
        public Team getLowestPointsTeam() { return lowestPointsTeam; }
        public void setLowestPointsTeam(Team lowestPointsTeam) { this.lowestPointsTeam = lowestPointsTeam; }
    }
}

