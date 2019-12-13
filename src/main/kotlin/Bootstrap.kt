import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
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
        val icpc2CodeValue: String,
        val icpc2FullText: String,
        val icd10CodeValue: String,
        val icd10Text: String
) {
    val icpc2EnumName = icpc2CodeValue.replace("-", "NEGATIVE_")
}

fun generateDiagnoseCodes(outputDirectory: Path) {
    val connection = URL("https://ehelse.no/kodeverk/icpc-2.den-internasjonale-klassifikasjonen-for-primaerhelsetjenesten/_/attachment/download/a952465b-1233-44cf-83db-c4918fbeb962:0c181f545aa52754922fc406663c075574447e28/Konverteringsfil%20ICPC-2%20til%20ICD-10%202019%20-%20oppdatert%2007.10.2019.txt").openConnection() as HttpURLConnection

    val entries = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
        reader.readLines()
                .filter { !it.matches(Regex(".?--.+")) }
                .map { CSVParser.parse(it, CSVFormat.DEFAULT.withDelimiter(';')) }.flatMap { it.records }
                .map {
                    Entry (icpc2CodeValue = it[0], icpc2FullText = it[1], icd10CodeValue = it[3], icd10Text = it[4])
                }
    }

    Files.newBufferedWriter(outputDirectory.resolve("ICPC2.json")).use { writer ->
        writer.write("[")
        writer.write(entries
                .groupBy { it.icpc2CodeValue }
                .map {
                    (_, entries) ->
                    val firstEntry = entries.first()
                    "{  \"code\": \"${firstEntry.icpc2CodeValue}\", \"text\":\"${firstEntry.icpc2FullText}\", \"mapsTo:\": ${entries.joinToString(", ", "[", "]") { "\"${it.icd10CodeValue}\"" }}}"
                }
                .joinToString(",\n")
        )
        writer.write("]\n")
    }

    Files.newBufferedWriter(outputDirectory.resolve("ICD10.json")).use { writer ->
        writer.write("[")
        writer.write(entries
                .groupBy { it.icd10CodeValue }
                .map {
                    (_, entries) ->
                    val firstEntry = entries.first()
                    "{  \"code\": \"${firstEntry.icd10CodeValue}\", \"text\":\"${firstEntry.icd10Text}\", \"mapsTo:\": ${entries.joinToString(", ", "[", "]") { "\"${it.icpc2EnumName}\"" }}}"
                }
                .joinToString(",\n")
        )
        writer.write("]\n")
    }
    connection.disconnect()
}