/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package fr.ferret.model.legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.SwingWorker;
import htsjdk.tribble.readers.TabixReader;

/**
 * Classe globale pour objectif de traiter les données de serveur 1000 Génomes
 *
 * @author younes
 */
public class Data1KgModel extends SwingWorker<Integer, String> {

    String ftpAdress;
    LocusModel[] queries;
    boolean allSNPsFound;
    boolean noSNPFound;

    public Data1KgModel(String ftpAdress, LocusModel[] queries, boolean allSNPsFound) {
        this.ftpAdress = ftpAdress;
        this.queries = queries;
        this.allSNPsFound = allSNPsFound;
    }

    public String getFtpAdress() {
        return ftpAdress;
    }

    public void setFtpAdress(String ftpAdress) {
        this.ftpAdress = ftpAdress;
    }

    public LocusModel[] getQueries() {
        return queries;
    }

    public void setQueries(LocusModel[] queries) {
        this.queries = queries;
    }

    public boolean isAllSNPsFound() {
        return allSNPsFound;
    }

    public void setAllSNPsFound(boolean allSNPsFound) {
        this.allSNPsFound = allSNPsFound;
    }

    public void geneProcess(LinkedList<ElementSaisiModel> geneQueries, SettingsModel settings) {
        publish("Looking up gene locations...");
        FoundGeneAndRegion[] geneLocationFromGeneName = {null};
        if (geneQueries.get(0).getClass().getSimpleName().equals("GeneByNameModel")) {
            String[] geneList;
            geneList = new String[geneQueries.size()];
            for (int e = 0; e < geneQueries.size(); e++) {
                geneList[e] = ((GenebyNameModel) (geneQueries.get(e))).getName();
            }
            geneLocationFromGeneName[0] =
                    NCBIData.getQueryFromGeneName(geneList, settings.isVersionHG());
        } else {
            String[] geneList;
            geneList = new String[geneQueries.size()];
            for (int e = 0; e < geneQueries.size(); e++) {
                geneList[e] = Integer.toString(((GenebyIDModel) geneQueries.get(e)).getId());
            }
            geneLocationFromGeneName[0] =
                    NCBIData.getQueryFromGeneID(geneList, settings.isVersionHG());
        }
    }

    public void variantProcess(SettingsModel settings, LinkedList<ElementSaisiModel> snpQueries) {
        publish("Looking up variant locations...");
        LinkedList<String> chromosome = new LinkedList<>();
        LinkedList<String> startPos = new LinkedList<>();
        LinkedList<String> endPos = new LinkedList<>();
        ArrayList<String> SNPsFound = new ArrayList<>();
        boolean allSNPsFound = true;
        try {
            // pour chacun des variants entrés
            for (int i = 0; i < snpQueries.size(); i++) {
                URL urlLocation = new URL(
                        "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_gene.cgi?connect=&rs="
                                + snpQueries.get(i));
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(urlLocation.openStream()));
                String currentString = br.readLine();
                if (settings.isVersionHG()) {
                    while (currentString != null && !currentString.contains("\"GRCh37.p13\" : [")) {
                        currentString = br.readLine();
                    }
                } else {
                    while (currentString != null && !currentString.contains("\"GRCh38.p2\" : [")) {
                        currentString = br.readLine();
                    }
                }
                boolean chromosomeFound = false, startFound = false, endFound = false,
                        locatedOnInvalidChr = false;
                while (!(startFound && endFound && chromosomeFound)
                        && (currentString = br.readLine()) != null) {
                    String substring = currentString.substring(currentString.indexOf(" : \"") + 4,
                            currentString.indexOf("\","));
                    if (currentString.contains("\"chrPosFrom\"")) {
                        startPos.add(substring);
                        startFound = true;
                    } else if (currentString.contains("\"chr\"")) {
                        chromosome.add(substring);
                        locatedOnInvalidChr = chromosome.peekLast().equals("X")
                                | chromosome.peekLast().equals("Y")
                                | chromosome.peekLast().equals("MT");
                        chromosomeFound = true;
                    } else if (currentString.contains("\"chrPosTo\"")) {
                        endPos.add(substring);
                        endFound = true;
                    }
                }
                if (!(startFound && endFound && chromosomeFound && !locatedOnInvalidChr)) {
                    if (startFound) {
                        startPos.removeLast();
                    }
                    if (endFound) {
                        endPos.removeLast();
                    }
                    if (chromosomeFound) {
                        chromosome.removeLast();
                    }
                    allSNPsFound = false;
                } else {
                    SNPsFound.add(Integer.toString(((VariantbyIDModel) snpQueries.get(i)).getId()));
                }
                br.close();
            }
            this.allSNPsFound = allSNPsFound;
            this.noSNPFound = SNPsFound.isEmpty();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isInt(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // sortLocus fonctionne bien
    public static ArrayList<LocusModel> sortLocus(LocusModel[] queries) {

        ArrayList<ArrayList<LocusModel>> sortedByChromosome = new ArrayList<>(23);
        for (int i = 0; i < 23; i++) {
            sortedByChromosome.add(new ArrayList<>());
        }

        for (LocusModel querie : queries) {
            sortedByChromosome.get(querie.chrToInt() - 1).add(querie);
        }
        ArrayList<LocusModel> sortedQueries = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            ArrayList<LocusModel> currentList = sortedByChromosome.get(i);
            Collections.sort(currentList);
            int j = 0;
            while (j < currentList.size()) {
                String currentChr = currentList.get(j).getChromosome();
                int minPos = currentList.get(j).getStart(), maxPos = currentList.get(j++).getEnd();
                while (j < currentList.size() && currentList.get(j).getStart() <= maxPos) {
                    maxPos = currentList.get(j++).getEnd();
                }
                sortedQueries.add(new LocusModel(currentChr, minPos, maxPos));
            }
        }
        return sortedQueries;
    }

    // getPopulation_Indices est fonctionnelle
    public static HashMap<String, Integer> getPopulation_Indices(String fileName)
            throws IOException {
        HashMap<String, Integer> peopleSet = new HashMap<>(3500);

        TabixReader tabReader = new TabixReader(fileName);
        String s = tabReader.readLine();
        while (!s.contains("CHROM")) {
            s = tabReader.readLine();
        }
        String[] peopleStringArray = s.split("\t");
        for (int i = 0; i < peopleStringArray.length; i++) {
            peopleSet.put(peopleStringArray[i], i);
        }

        return peopleSet;
    }

    // getPeopleString_Phase3 fonctionne très bien
    public static String getPeopleString_Phase3(String NumOfChr) throws IOException {
        // helper method for the below method
        String result = null;
        try {
            String webAddress =
                    "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chr" + NumOfChr
                            + ".phase3_shapeit2_mvncall_integrated_v5b.20130502.genotypes.vcf.gz";
            TabixReader ourtab = new TabixReader(webAddress);
            result = ourtab.readLine();
            while (!result.contains("CHROM")) {
                result = ourtab.readLine();
            }
            ourtab.close();
        } catch (RuntimeException e) {
        }
        return result;
    }

    // getPeopleString_Phase3_GRCh38, il marche pas car le répertoire suppoting n'est pas
    // disponible.
    public static String getPeopleString_Phase3_GRCh38(String NumOfChr) throws IOException {
        String result = null;
        try {
            String webAddress =
                    "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/supporting/GRCh38_positions/ALL.chr"
                            + NumOfChr
                            + ".phase3_shapeit2_mvncall_integrated_v3plus_nounphased.rsID.genotypes.GRCh38_dbSNP_no_SVs.vcf.gz";
            TabixReader tabReader = new TabixReader(webAddress);
            result = tabReader.readLine();
            while (!result.contains("CHROM")) {
                result = tabReader.readLine();
            }
        } catch (RuntimeException e) {
        }
        return result;
    }

    // getPeopleString_Phase1 fonctionne bien
    public static String getPeopleString_Phase1(String NumOfChr) throws IOException {
        String result = null;
        try {
            String webAddress = "http://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20110521/ALL.chr"
                    + NumOfChr + ".phase1_release_v3.20101123.snps_indels_svs.genotypes.vcf.gz";
            TabixReader tabReader = new TabixReader(webAddress);
            result = tabReader.readLine();
            while (!result.contains("CHROM")) {
                result = tabReader.readLine();
            }
        } catch (RuntimeException e) {
        }
        return result;
    }

    public void process1KG(SettingsModel settings, LinkedList<ElementSaisiModel> enteredElements)
            throws IOException {

        if (enteredElements != null
                && enteredElements.get(0).getClass().getSimpleName().equals("VariantModel")) {
            variantProcess(settings, enteredElements);
        } else if (enteredElements != null
                && enteredElements.get(0).getClass().getSimpleName().startsWith("GeneModel")) {
            geneProcess(enteredElements, settings);
        }
        publish("Parsing Individuals...");

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        // Initalize some variables
        ArrayList<String> peopleOfInterest = new ArrayList<String>();
        int countVariant = 0, countPeople = 0;
        String[][] genotypes = null;

        ArrayList<LocusModel> sortedLocus = sortLocus(queries);
        String webAddress = ftpAdress.replace('$', '1');
        HashMap<String, Integer> peopleSearched = null;
        try {
            peopleSearched = getPopulation_Indices(webAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int queryNumber = sortedLocus.size();
        int[] querySize = new int[queryNumber + 1];
        querySize[0] = 0;
        for (int i = 0; i < sortedLocus.size(); i++) {
            querySize[i + 1] =
                    querySize[i] + (sortedLocus.get(i).getEnd() - sortedLocus.get(i).getStart());
        }
        try {
            String s;
            BufferedReader popBuffRead = null;
            if (ftpAdress.contains("qlq chose qui précise qu'on veut travailler sur la phase 1")) {
                popBuffRead = new BufferedReader(new InputStreamReader(Data1KgModel.class
                        .getClassLoader().getResourceAsStream("samplesPhase1.txt")));
            } else {
                popBuffRead = new BufferedReader(new InputStreamReader(Data1KgModel.class
                        .getClassLoader().getResourceAsStream("samplesPhase3.txt")));
            }
            // Création des population intéressé par l'utilisateur avec leurs indices dans la liste
            // PeopleOfInterest
            String[] IDs;
            s = popBuffRead.readLine();
            // the case where the user choose all the population
            if (!enteredElements.get(0).contain("ALL")) {
                while (s != null) {
                    IDs = s.split("\t");
                    if (enteredElements.get(0).contain(IDs[1])
                            || enteredElements.get(0).contain(IDs[2])) {
                        if (peopleSearched.containsKey(IDs[0])) {
                            peopleOfInterest.add(IDs[0]);
                            countPeople++;
                        }
                    }
                    s = popBuffRead.readLine();
                }
                // the case where the user choose some populations
            } else {
                while (s != null) {
                    IDs = s.split("\t");
                    if (peopleSearched.containsKey(IDs[0])) {
                        peopleOfInterest.add(IDs[0]);
                        countPeople++;
                    }
                    s = popBuffRead.readLine();
                }
            }
            popBuffRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publish("Downloading Data from 1000 Genomes...");
        String s;
        BufferedWriter vcfWrite = null;
        long startTime = 0;
        Integer tempInt = null;

        // écriture des fichiers vcf
        if (settings.getOutput().equals("vcf")) {
            // Creation de fichier .vcf avec le nom Ferret_Datayyyy_MM_dd_HH_mm_ss.vcf
            String outputFileName;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            outputFileName = "Ferret_Data" + dtf.format(now);
            File vcfWriteFile = new File(outputFileName + ".vcf");
            try {
                vcfWriteFile.createNewFile();
                vcfWrite = new BufferedWriter(new FileWriter(vcfWriteFile));
                TabixReader treader = new TabixReader(webAddress);
                s = treader.readLine();
                // Ecriture de "Header" du fichier .vcf
                while (!s.contains("CHROM")) {
                    vcfWrite.write(s);
                    vcfWrite.newLine();
                    s = treader.readLine();
                }
                String[] stringSplit = s.split("\t");

                for (int i = 0; i < 9; i++) {
                    vcfWrite.write(stringSplit[i] + "\t");
                }
                ArrayList<Integer> IndicesDePopulation;
                IndicesDePopulation = new ArrayList<Integer>();
                // Write individuals
                for (int i = 9; i < stringSplit.length; i++) {
                    if (peopleOfInterest.contains(stringSplit[i])) {
                        vcfWrite.write(stringSplit[i] + "\t");
                        IndicesDePopulation.add(i);
                    }
                }
                vcfWrite.newLine();

                for (int j = 0; j < queryNumber; j++) {
                    webAddress = ftpAdress.replace("$", sortedLocus.get(j).getChromosome());
                    treader = new TabixReader(webAddress);
                    startTime = System.nanoTime();
                    TabixReader.Iterator iter = treader.query(
                            sortedLocus.get(j).getChromosome() + ":" + sortedLocus.get(j).getStart()
                                    + "-" + sortedLocus.get(j).getEnd());
                    long endTime = System.nanoTime();
                    System.out.println("Tabix iterator time: " + (endTime - startTime));

                    while ((s = iter.next()) != null) {
                        countVariant++;
                        stringSplit = s.split("\t");

                        if (isInt(stringSplit[1])) {
                            tempInt = Integer.parseInt(stringSplit[1])
                                    - sortedLocus.get(j).getStart();
                            if (tempInt > 0) {
                                setProgress((int) ((tempInt + querySize[j])
                                        / (double) querySize[queryNumber] * 99));
                            }
                        }

                        String[] multiAllele = stringSplit[4].split(",");
                        int[] variantFreq = new int[multiAllele.length + 1];

                        int chromosomeCount = 0;

                        StringBuilder tempString = new StringBuilder();
                        for (int i = 0; i < 9; i++) {
                            tempString.append(stringSplit[i]).append("\t");
                        }
                        for (int i = 9; i < stringSplit.length; i++) {
                            if (IndicesDePopulation.contains(i)) {
                                tempString.append(stringSplit[i]).append("\t");
                                variantFreq[Character.getNumericValue(stringSplit[i].charAt(0))]++;
                                variantFreq[Character.getNumericValue(stringSplit[i].charAt(0))]++;
                                chromosomeCount += 2;
                            }
                        }
                        boolean tempBoolean = true;
                        for (int i = 0; i < variantFreq.length; i++) {
                            if ((variantFreq[i] / (float) chromosomeCount) < settings.getMaf()) {
                                tempBoolean = false;
                            }
                        }
                        if (tempBoolean) {
                            vcfWrite.write(tempString.toString());
                            vcfWrite.newLine();
                        }
                    }

                }
                vcfWrite.close();
                if (countVariant == 0) {
                    vcfWriteFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                vcfWriteFile.delete();
            }
        } else {

            try {
                String fileName;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();
                fileName = "Ferret_Data_genotypes" + dtf.format(now);
                File vcfFile = new File(fileName + ".vcf");
                vcfFile.createNewFile();
                BufferedWriter vcfBuffWrite = new BufferedWriter(new FileWriter(vcfFile));
                for (int j = 0; j < queryNumber; j++) {
                    webAddress = ftpAdress.replace("$", sortedLocus.get(j).getChromosome());
                    TabixReader tr = new TabixReader(webAddress);

                    // Get the iterator
                    startTime = System.nanoTime();
                    TabixReader.Iterator iter = tr.query(sortedLocus.get(j).getChromosome() + ":"
                            + sortedLocus.get(j).getStart() + "-" + sortedLocus.get(j).getEnd());
                    long endTime = System.nanoTime();
                    System.out.println("Tabix iterator time: " + (endTime - startTime));
                    while ((s = iter.next()) != null) {
                        countVariant++;
                        String[] stringSplit = s.split("\t");
                        if (stringSplit[6].equals("PASS")) {
                            if (isInt(stringSplit[1])) {
                                tempInt = Integer.parseInt(stringSplit[1])
                                        - sortedLocus.get(j).getStart();
                                if (tempInt > 0) {
                                    setProgress((int) ((tempInt + querySize[j])
                                            / (double) querySize[queryNumber] * 99));
                                }
                            }
                            if (stringSplit[2].equals(".")) {
                                stringSplit[2] = "chr" + sortedLocus.get(j).getChromosome() + "_"
                                        + stringSplit[1];
                                for (int i = 0; i < stringSplit.length; i++) {
                                    vcfBuffWrite.write(stringSplit[i] + "\t");
                                }
                            } else {
                                vcfBuffWrite.write(s);
                            }
                            vcfBuffWrite.newLine();
                        } else {
                            vcfBuffWrite.write(s);
                            vcfBuffWrite.newLine();
                        }
                    }
                    long endEndTime = System.nanoTime();
                    System.out.println("Iteration time: " + (endEndTime - endTime));
                }
                vcfBuffWrite.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("Null Pointer Exception " + tempInt);
                // Tabix iterator doesn't have has.next method, so this protects from regions
                // without variants
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.out.println("Runtime Exception" + tempInt);
                System.out.println("Iteration time: " + (System.nanoTime() - startTime));
            }
        }
        if (countVariant == 0) {
            String fileName;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            fileName = "Ferret_Data" + dtf.format(now);
            File vcfFile = new File(fileName + "_genotypes.vcf");
            vcfFile.delete();
        }

        publish("Outputting files...");
        try {
            // Creating lookup HashMap for family info, etc.
            HashMap<String, String[]> familyInfo = new HashMap<String, String[]>(5000);
            BufferedReader familyInfoRead = new BufferedReader(new InputStreamReader(
                    Data1KgModel.class.getClassLoader().getResourceAsStream("family_info.txt")));
            s = familyInfoRead.readLine();
            while ((s = familyInfoRead.readLine()) != null) {
                String[] text = s.split("\t");
                String[] temp = {text[0], text[2], text[3], text[4]};
                familyInfo.put(text[1], temp);
            }
            familyInfoRead.close();

            BufferedWriter mapWrite = null, infoWrite = null, pedWrite = null, frqWrite = null;
            boolean fileEmpty = true, frqFileEmpty = true;

            String fileName;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            fileName = "Ferret_Data" + dtf.format(now);
            if (settings.getOutput().equals("all")) {
                genotypes = new String[countPeople + 1][2 * countVariant + 6];

                File mapFile = new File(fileName + ".map");
                mapFile.createNewFile();
                mapWrite = new BufferedWriter(new FileWriter(mapFile));

                File infoFile = new File(fileName + ".info");
                infoFile.createNewFile();
                infoWrite = new BufferedWriter(new FileWriter(infoFile));

                File pedFile = new File(fileName + ".ped");
                pedFile.createNewFile();
                pedWrite = new BufferedWriter(new FileWriter(pedFile));
            }

            BufferedReader vcfRead =
                    new BufferedReader(new FileReader(fileName + "_genotypes.vcf"));

            File freqFile = new File(fileName + "AlleleFreq.frq");
            freqFile.createNewFile();
            frqWrite = new BufferedWriter(new FileWriter(freqFile));
            frqWrite.write(
                    "CHROM\tVARIANT\tPOS\tALLELE1\tALLELE2\tNB_CHR\t1KG_A1_FREQ\t1KG_A2_FREQ");
            frqWrite.newLine();

            // Populate the genotypes array with patient/family data
            if (settings.getOutput().equals("all")) {
                for (int i = 0; i < countPeople; i++) {
                    genotypes[i + 1][1] = peopleOfInterest.get(i);
                    String[] temp = familyInfo.get(genotypes[i + 1][1]);
                    genotypes[i + 1][0] = temp[0];
                    genotypes[i + 1][2] = temp[1];
                    genotypes[i + 1][3] = temp[2];
                    genotypes[i + 1][4] = temp[3];
                    genotypes[i + 1][5] = "0";
                }
            }

            int index = 0;
            int espErrorCount = 0;
            // System.out.println("Got to reading vcf part");
            while ((s = vcfRead.readLine()) != null) {
                String[] text = s.split("\t");
                // populate variables about this variant
                String[] variantPossibilities;
                String[] multAllele = text[4].split(",");
                int[] variantFreq = {0, 0};
                variantPossibilities = new String[multAllele.length + 1];
                variantPossibilities[0] = text[3];
                for (int i = 0; i < multAllele.length; i++) {
                    variantPossibilities[i + 1] = multAllele[i];
                }
                // indel trick
                String[] retainedPossibility = new String[2];
                if (!text[4].contains("CN") && variantPossibilities.length == 2
                        && (variantPossibilities[0].length() > 1
                                || variantPossibilities[1].length() > 1)) {
                    text[2] = "indel_" + text[2] + "_" + variantPossibilities[0] + "/"
                            + variantPossibilities[1];
                    retainedPossibility[0] = variantPossibilities[0];
                    retainedPossibility[1] = variantPossibilities[1];
                    variantPossibilities[0] = "A";
                    variantPossibilities[1] = "T";
                }
                int numChr = 0;
                double freqZero = 0, freqOne = 0;
                if (variantPossibilities.length == 2) {
                    if (!text[4].contains("CN")) {
                        for (int i = 0; i < peopleOfInterest.size(); i++) {
                            String tempPerson = peopleOfInterest.get(i);
                            int personIndex = peopleSearched.get(tempPerson);
                            String personGtype = text[personIndex];
                            int temp = Character.getNumericValue(personGtype.charAt(0));
                            if (settings.getOutput().equals("all")) {
                                genotypes[i + 1][2 * index + 6] = variantPossibilities[temp];
                            }
                            variantFreq[temp]++;
                            temp = Character.getNumericValue(personGtype.charAt(2));
                            if (settings.getOutput().equals("all")) {
                                genotypes[i + 1][2 * index + 7] = variantPossibilities[temp];
                            }
                            variantFreq[temp]++;
                        }
                    } else if (text[4].contains("CN")) {
                        for (int i = 0; i < peopleOfInterest.size(); i++) {
                            String tempPerson = peopleOfInterest.get(i);
                            int personIndex = peopleSearched.get(tempPerson);
                            String personGtype = text[personIndex];
                            int temp = Character.getNumericValue(personGtype.charAt(0));
                            variantFreq[temp]++;
                            temp = Character.getNumericValue(personGtype.charAt(2));
                            variantFreq[temp]++;
                        }
                    }
                    numChr = variantFreq[0] + variantFreq[1];
                    freqZero = Math.round(variantFreq[0] / ((double) numChr) * 10000) / 10000.0;
                    freqOne = Math.round(variantFreq[1] / ((double) numChr) * 10000) / 10000.0;
                    if (settings.getOutput().equals("all")) {
                        if (!text[4].contains("CN")) {
                            genotypes[0][2 * index + 6] = Double.toString(freqZero);
                            genotypes[0][2 * index + 7] = Double.toString(freqZero);
                            index++;
                        }
                        if (!text[4].contains("CN") && (freqZero >= settings.getMaf()
                                && freqOne >= settings.getMaf())) {
                            fileEmpty = false;
                            mapWrite.write(text[0] + "\t" + text[2] + "\t0\t" + text[1]);
                            mapWrite.newLine();
                            infoWrite.write(text[2] + "\t" + text[1]);
                            infoWrite.newLine();
                        }
                    }
                }
                if (text[2].contains("indel")) {
                    variantPossibilities = retainedPossibility;
                }
                if (variantPossibilities.length == 2 && freqOne >= settings.getMaf()
                        && freqZero >= settings.getMaf()) {
                    frqFileEmpty = false;
                    frqWrite.write(text[0] + "\t" + text[2] + "\t" + text[1] + "\t"
                            + variantPossibilities[0] + "\t" + variantPossibilities[1] + "\t"
                            + numChr + "\t" + df.format(freqZero) + "\t" + df.format(freqOne));
                    frqWrite.newLine();
                }

            }
            if (settings.getOutput().equals("all")) {
                for (int i = 0; i < countPeople; i++) {
                    for (int j = 0; j <= 5; j++) {
                        pedWrite.write(genotypes[i + 1][j] + "\t");
                    }
                    for (int j = 6; j < index * 2 + 6; j++) {
                        if (Double.parseDouble(genotypes[0][j]) >= settings.getMaf()
                                && (1 - Double.parseDouble(genotypes[0][j])) >= settings.getMaf()) {
                            pedWrite.write(genotypes[i + 1][j] + "\t");
                        }
                    }
                    pedWrite.newLine();
                }
                pedWrite.close();
                mapWrite.close();
                infoWrite.close();
                if (fileEmpty) {
                    new File(fileName + ".ped").delete();
                    new File(fileName + ".info").delete();
                    new File(fileName + ".map").delete();
                }
            }
            frqWrite.close();
            vcfRead.close();
            File vcfFile = new File(fileName + "_genotypes.vcf");
            vcfFile.delete();
            if (frqFileEmpty) {
                freqFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setProgress(100);
        System.out.println("Finished");
    }

    @Override
    protected Integer doInBackground() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of
                                                                       // generated methods, choose
                                                                       // Tools | Templates.
    }
}
