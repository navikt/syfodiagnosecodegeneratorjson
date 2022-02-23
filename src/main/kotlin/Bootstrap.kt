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

    val icd10Url = URL("https://www.ehelse.no/kodeverk/kodeverket-icd-10-og-icd-11/_/attachment/download/213ee9ea-7966-48f7-9130-fe1e603872bf:164e4276b153b878738c7ae35ec29dbcc2edf821/Kodeliste%20ICD-10%202022%20korrigert%20(excel)%20.xlsx").openConnection() as HttpURLConnection
    val icpc2Url = URL("https://www.ehelse.no/kodeverk/icpc-2.den-internasjonale-klassifikasjonen-for-primaerhelsetjenesten/_/attachment/download/ea12e804-70cf-4e81-94c6-5bde3fcbea69:e92bdeb281e037997a1dc78e0ff663b816535702/Koderegister%2060%20tegn%20uten%20prosesskoder%20-%2020.10.2020.txt").openConnection() as HttpURLConnection


    val icd10KomplettWorkbook: Workbook = XSSFWorkbook(icd10Url.inputStream)
    val icd10KomplettSheet: Sheet = icd10KomplettWorkbook.getSheetAt(0)

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
                .map { CSVParser.parse(it, CSVFormat.Builder.create().setDelimiter(',').build()) }.flatMap { it.records }
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