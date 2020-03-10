import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path


fun main() {
    val outputDirectory = File("src/main/resources")
    val sourcePackage = outputDirectory.resolve("json").toPath()
    Files.createDirectories(sourcePackage)
    generateDiagnoseCodes(sourcePackage)
}

data class Entry(
        val codeValue: String,
        val text: String
)

fun generateDiagnoseCodes(outputDirectory: Path) {

    val icd10Url = URL("https://ehelse.no/kodeverk/kodeverket-icd-10-og-icd-11/_/attachment/download/5a7a2b73-f136-4720-8557-f17216a6a8d9:1355ea5594270eb7aef19532d19f21a2a007071d/ICD-10%202020%20-%20oppdatert%2004.02.2020.xlsx").openConnection() as HttpURLConnection
    val icpc2Url = URL("https://ehelse.no/kodeverk/icpc-2.den-internasjonale-klassifikasjonen-for-primaerhelsetjenesten/_/attachment/download/84fd2aba-0907-4a63-b792-30a16412f050:9f5cd336597a80bcffb5194d71a18437e7cc47c7/Koderegister%20med%20utvidet%20termsett%20(basert%20p%C3%A5%20icd-10%20mapping)%2006.03.2020.txt").openConnection() as HttpURLConnection


    val icd10KomplettWorkbook: Workbook = XSSFWorkbook(icd10Url.inputStream)
    val icd10KomplettSheet: Sheet = icd10KomplettWorkbook.getSheetAt(1)

    val icd10Entries = icd10KomplettSheet
            .filter { !it.getCell(0).stringCellValue.toString().matches(Regex("Kode"))  }
            .filter { !it.getCell(0).stringCellValue.toString().matches(Regex(""))  }
            .map {row -> Entry (
                codeValue = row.getCell(0).stringCellValue.toString(),
                text = row.getCell(2).stringCellValue.toString())
    }

    val icpc2Entries = BufferedReader(InputStreamReader(icpc2Url.inputStream)).use { reader ->
        reader.readLines()
                .filter { !it.matches(Regex(".?--.+")) }
                .map { CSVParser.parse(it, CSVFormat.DEFAULT.withDelimiter(',')) }.flatMap { it.records }
                .map {
                    Entry (codeValue = it[0], text = it[1])
                }
    }

    Files.newBufferedWriter(outputDirectory.resolve("ICPC2.json")).use { writer ->
        writer.write("[")
        writer.write(icpc2Entries
                .groupBy { it.codeValue }
                .map {
                    (_, entries) ->
                    val firstEntry = entries.first()
                    "{\"code\":\"${firstEntry.codeValue}\",\"text\":\"${firstEntry.text}\"}"
                }
                .joinToString(",")
        )
        writer.write("]\n")
    }



    Files.newBufferedWriter(outputDirectory.resolve("ICD10.json")).use { writer ->
        writer.write("[")
        writer.write(icd10Entries
                .groupBy { it.codeValue }
                .map {
                    (_, entries) ->
                    val firstEntry = entries.first()
                    "{\"code\":\"${firstEntry.codeValue}\",\"text\":\"${firstEntry.text.replace(Regex("[^a-zA-Z0-9 ]"), "'")}\"}"
                }
                .joinToString(",")
        )

        writer.write("]\n")
    }

    icd10Url.disconnect()
    icpc2Url.disconnect()
}