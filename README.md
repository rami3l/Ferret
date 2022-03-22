# Ferret

[![Java Version](https://img.shields.io/badge/Java-v17-red?style=flat-square)](https://openjdk.java.net/projects/jdk/17)
[![Gradle Version](https://img.shields.io/badge/Gradle-v7-red?style=flat-square)](https://gradle.org/install/)
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

However, it might be useful to delete the cache folder at `~/config/ferret`.

## References

`Ferret` v3 wouldn't be possible without the previous PAPPL projects of our classmates:

- Frontend: <https://github.com/PAPPL-Ferret/NewFerret>
- Backend: <https://github.com/JorgeStone/Ferret-Model>
