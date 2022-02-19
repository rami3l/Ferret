package fr.ferret.model.utils;

import fr.ferret.controller.exceptions.FileContentException;
import fr.ferret.controller.exceptions.FileFormatException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    @Test void getCsvDelimiter_withValidExtension_shouldReturnDelimiter() {
        var delim1 = FileReader.getCsvDelimiter("file.csv");
        var delim2 = FileReader.getCsvDelimiter(".tab");
        var delim3 = FileReader.getCsvDelimiter("name with spaces.tsv");
        var delim4 = FileReader.getCsvDelimiter("compose.name.txt");
        assertTrue(delim1.isPresent());
        assertTrue(delim2.isPresent());
        assertTrue(delim3.isPresent());
        assertTrue(delim4.isPresent());
        assertEquals(",", delim1.get());
        assertEquals("\t", delim2.get());
        assertEquals("\t", delim3.get());
        assertEquals(" ", delim4.get());
    }

    @Test void getCsvDelimiter_withInvalidOrNoExtension_shouldReturnOptionalEmpty(){
        assertTrue(FileReader.getCsvDelimiter("no_extension").isEmpty());
        assertTrue(FileReader.getCsvDelimiter("file.invalid_ext").isEmpty());
        assertTrue(FileReader.getCsvDelimiter("no_extension").isEmpty());
        assertTrue(FileReader.getCsvDelimiter("no_extension").isEmpty());
    }

    @Test void readValidCsv_shouldWork() {
        String invalidRegex = ".*\\D.*";
        List<String> elements = null;
        try {
            elements = FileReader.readCsvLike("src/test/resources/valid.csv", invalidRegex);
        } catch (IOException e) {
            fail("File should have be read without errors", e);
        }
        assertEquals(8, elements.size());
        assertEquals("123", elements.get(0));
        assertEquals("4", elements.get(1));
        assertEquals("654321", elements.get(2));
        assertEquals("2", elements.get(3));
        assertEquals("8", elements.get(4));
        assertEquals("73", elements.get(5));
        assertEquals("890", elements.get(6));
        assertEquals("99", elements.get(7));
    }

    @Test void readCsv_withInvalidCharacter_shouldThrowFileContentException() {
        System.out.println("---");
        String invalidRegex = ".*\\D.*";
        assertThrowsExactly(
            FileContentException.class,
            () -> FileReader.readCsvLike("src/test/resources/invalid.csv", invalidRegex)
        );
    }

    @Test void readFile_withInvalidExtension_shouldThrowFileFormatException() {
        String invalidRegex = ".*\\D.*";
        assertThrowsExactly(
            FileFormatException.class,
            () -> FileReader.readCsvLike("src/test/resources/file.invalidExt", invalidRegex)
        );
    }

}
