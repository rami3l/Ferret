# Ferret

[![Version](https://img.shields.io/github/v/release/rami3l/Ferret?style=flat-square)][releases]
[![License](https://img.shields.io/github/license/rami3l/Ferret?style=flat-square)](LICENSE)

A user-friendly tool to extract data from the 1000 Genomes Project.

---

## Contents

- [Ferret](#ferret)
  - [Contents](#contents)
  - [Features](#features)
  - [Installation](#installation)
    - [Prebuilt Executable](#prebuilt-executable)
    - [Build From Source](#build-from-source)
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

## Installation

### Prebuilt Executable

See the [`releases`][releases] page.

[releases]: https://github.com/rami3l/Ferret/releases

### Build From Source

With the latest JDK installed:

- To try out this application, just execute:

  ```bash
  ./gradlew run
  ```

- To build a fat `.jar` executable under `./build/libs`:

  ```bash
  ./gradlew jar
  ```

## References

Ferret v3 wouldn't be possible without the previous PAPPL projects of our classmates:

- Frontend: <https://github.com/PAPPL-Ferret/NewFerret>
- Backend: <https://github.com/JorgeStone/Ferret-Model>
