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

    val icd10Url = URL("https://ehelse.no/kodeverk/kodeverket-icd-10-og-icd-11/_/attachment/download/695fc467-d25d-4429-b065-c4e40c9cc067:8dcf17eae799876cf9f2a4b5b16fe339051540be/Kodeliste%20ICD-10%202020%20(Excel)%20-%20oppdatert%2026.03.2020.xlsx").openConnection() as HttpURLConnection
    val icpc2Url = URL("https://ehelse.no/kodeverk/icpc-2.den-internasjonale-klassifikasjonen-for-primaerhelsetjenesten/_/attachment/download/2dc257eb-d789-41fd-a006-58dbdf4e5bb5:03426b33938428ee6ed622c9df6db8992146fc62/Koderegister%2060%20tegn%20med%20fullstendig%20sett%20prosesskoder%20(til%20NAV).txt%20-%2004.05.2020.txt").openConnection() as HttpURLConnection


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
                    "{\"code\":\"${firstEntry.codeValue}\",\"text\":\"${firstEntry.text.replace("\"", "\\\"")}\"}"
                }
                .joinToString(",")
        )

        writer.write("]\n")
    }

    icd10Url.disconnect()
    icpc2Url.disconnect()
}