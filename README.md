# Ferret

[![Java Version](https://img.shields.io/badge/Java-v17-red?style=flat-square)](https://openjdk.java.net/projects/jdk/17)
[![Gradle Version](https://img.shields.io/badge/Gradle-v7-red?style=flat-square)](https://gradle.org/install/)
[![Javadoc](https://img.shields.io/badge/API-Javadoc-red?style=flat-square)](https://rami3l.github.io/Ferret/)
[![Release](https://img.shields.io/github/v/release/rami3l/Ferret?include_prereleases&style=flat-square)][releases]
[![License](https://img.shields.io/github/license/rami3l/Ferret?style=flat-square)](LICENSE)

A user-friendly tool to extract data from the 1000 Genomes Project.

---

## Contents

- [Ferret](#ferret)
  - [Contents](#contents)
  - [Features](#features)
  - [Getting Started](#getting-started)
    - [Prebuilt Executable](#prebuilt-executable)
    - [Build From Source](#build-from-source)
  - [Uninstallation](#uninstallation)
  - [References](#references)
  - [How to add a new phase](#how-to-add-a-new-phase)

---

## Features

- Query the 1000 Genomes project server:
  - By locus directly, or by gene/SNP (locus information retrieved from NCBI)
  - From a selection of populations worldwide
- Generate a selection of the following files:
  - [`.ped`]: The PLINK/MERLIN/Haploview text pedigree + genotype table file.
  - [`.map`]: The PLINK text fileset variant information file, usually used with a [`.ped`] file.
  - [`.info`]: The Haploview map file, usually used with a [`.ped`] file.
  - [`.frq`]: The allele frequency report file.

[`.ped`]: https://www.cog-genomics.org/plink2/formats#ped
[`.map`]: https://www.cog-genomics.org/plink2/formats#map
[`.info`]: https://www.cog-genomics.org/plink2/formats#info
[`.frq`]: https://www.cog-genomics.org/plink2/formats#frq

## Getting Started

As a portable, self-contained `.jar` executable, **no installation is required** to build or use `Ferret` as long as you have the latest [JDK](https://openjdk.java.net/projects/jdk) (`v17` as of March 2022) installed.

### Prebuilt Executable

- Download the `.jar` from the [`releases`][releases] page.

[releases]: https://github.com/rami3l/Ferret/releases

### Build From Source

- To try out this application, just execute:

  ```bash
  ./gradlew run
  ```

- To build a fat `.jar` executable under `./build/libs`:

  ```bash
  ./gradlew jar
  ```

## Uninstallation

No explicit uninstallation process is required.

However, it might be useful to delete the cache folder at `~/.config/ferret`.

## References

`Ferret` v3 wouldn't be possible without the previous PAPPL projects of our classmates:

- Frontend: <https://github.com/PAPPL-Ferret/NewFerret>
- Backend: <https://github.com/JorgeStone/Ferret-Model>

## How to add a new phase

### Add the sample (population) data
In the [sample folder](./src/main/resources/samples) add a new phase file (see existing phase files). 
Only the 3 first columns are needed, and reference it in the [phaseList.txt file](./src/main/resources/samples/phaseList.txt) 
(in the same folder) : the first column is the name of the phase, the second column specify how the phase will be displayed
in the interface (in the settings frame), and the third column is the name of the phase file.  
The columns must be separated with a ":". The third column is optional: if you don't specify it, the corresponding
phase will be displayed as disabled in the setting frame. It can be useful to add a phase before having
the phase file, or the 1kg information (but its interest is limited).

### Add the 1kg path and filename template
Finally, you must add the path and the filename template of the phase in the [server.properties file](./src/main/resources/server.properties)
(1kg._phasename_.path & 1kg._phasename_.filename). The filename is a template because it changes for each chromosome. All "{0}" part
in the filename are replaced by the chromosome during the execution of Ferret.
