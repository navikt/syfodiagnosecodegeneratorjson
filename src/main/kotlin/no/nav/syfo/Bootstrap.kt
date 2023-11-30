package no.nav.syfo

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path


val log: Logger = LoggerFactory.getLogger("no.nav.syfo")

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

    val icd10Url = URI.create("https://www.ehelse.no/kodeverk-og-terminologi/ICD-10-og-ICD-11/_/attachment/inline/4d2b7160-407d-417a-b848-112002cc025c:4246e4ef5745de04307a4d5ae3a2a23dd23dc47f/Kodeliste%20ICD-10%202023%20oppdatert%2013.12.22.xlsx").toURL().openConnection() as HttpURLConnection
    val icpc2Url = URI.create("https://www.ehelse.no/kodeverk-og-terminologi/ICPC-2/_/attachment/inline/bfa952b9-fbb5-49fe-963b-27024d573e71:3cdfa328cb7f9333a6707bb3bc079ce9d423174f/Fil%202%202023%20-%20ICPC-2%20teknisk%20koderegister%20med%20prosesskoder,%20fulltekst%20og%2060%20tegn%20tekst%20(kun%20en%20linje%20per%20kode)%20(Excel).xlsx").toURL().openConnection() as HttpURLConnection


    val icd10KomplettWorkbook: Workbook = XSSFWorkbook(icd10Url.inputStream)
    val icd10KomplettSheet: Sheet = icd10KomplettWorkbook.getSheetAt(0)

    val icd10Entries = icd10KomplettSheet
            .filter { !it.getCell(0).stringCellValue.toString().matches(Regex("Kode"))  }
            .filter { !it.getCell(0).stringCellValue.toString().matches(Regex(""))  }
            .map {row -> Entry (
                codeValue = row.getCell(0).stringCellValue.toString(),
                text = row.getCell(2).stringCellValue.toString())
    }

    val icpc2KomplettWorkbook: Workbook = XSSFWorkbook(icpc2Url.inputStream)
    val icpc2KomplettSheet: Sheet = icpc2KomplettWorkbook.getSheetAt(0)

    // This is a tmp fix, until Ehelse can produce a list without prosess koder....
    val icpc2ProsesCodes = listOf("A30","A31","A32","A33","A34","A35","A36","A37","A38","A39","A40","A41","A42","A43"
        ,"A44","A45","A46","A47","A48","A49","A50","A51","A52","A53","A54","A55","A56","A57","A58","A59","A60",
        "A61","A62","A63","A64","A65","A66","A67","A68","A69","B30","B31","B32","B33","B34","B35","B36","B37",
        "B38","B39","B40","B41","B42","B43","B44","B45","B46","B47","B48","B49","B50","B51","B52","B53","B54","B55",
        "B56","B57","B58","B59","B60","B61","B62","B63","B64","B65","B66","B67","B68","B69","D30","D31","D32","D33",
        "D34","D35","D36","D37","D38","D39","D40","D41","D42","D43","D44","D45","D46","D47","D48","D49","D50",
        "D51","D52","D53","D54","D55","D56","D57","D58","D59","D60","D61","D62","D63","D64","D65","D66","D67","D68",
        "D69","F30","F31","F32","F33","F34","F35","F36","F37","F38","F39","F40","F41","F42","F43","F44","F45","F46",
        "F47","F48","F49","F50","F51","F52","F53","F54","F55","F56","F57","F58","F59","F60","F61","F62","F63","F64",
        "F65","F66","F67","F68","F69","H30","H31","H32","H33","H34","H35","H36","H37","H38","H39","H40","H41","H42",
        "H43","H44","H45","H46","H47","H48","H49","H50","H51","H52","H53","H54","H55","H56","H57","H58","H59","H60",
        "H61","H62","H63","H64","H65","H66","H67","H68","H69","K30","K31","K32","K33","K34","K35","K36","K37","K38",
        "K39","K40","K41","K42","K43","K44","K45","K46","K47","K48","K49","K50","K51","K52","K53","K54","K55","K56",
        "K57","K58","K59","K60","K61","K62","K63","K64","K65","K66","K67","K68","K69","L30","L31","L32","L33","L34",
        "L35","L36","L37","L38","L39","L40","L41","L42","L43","L44","L45","L46","L47","L48","L49","L50","L51","L52",
        "L53","L54","L55","L56","L57","L58","L59","L60","L61","L62","L63","L64","L65","L66","L67","L68","L69","N30",
        "N31","N32","N33","N34","N35","N36","N37","N38","N39","N40","N41","N42","N43","N44","N45","N46","N47","N48",
        "N49","N50","N51","N52","N53","N54","N55","N56","N57","N58","N59","N60","N61","N62","N63","N64","N65","N66",
        "N67","N68","N69","P30","P31","P32","P33","P34","P35","P36","P37","P38","P39","P40","P41","P42","P43","P44",
        "P45","P46","P47","P48","P49","P50","P51","P52","P53","P54","P55","P56","P57","P58","P59","P60","P61","P62",
        "P63","P64","P65","P66","P67","P68","P69","R30","R31","R32","R33","R34","R35","R36","R37","R38","R39","R40",
        "R41","R42","R43","R44","R45","R46","R47","R48","R49","R50","R51","R52","R53","R54","R55","R56","R57","R58",
        "R59","R60","R61","R62","R63","R64","R65","R66","R67","R68","R69","S30","S31","S32","S33","S34","S35","S36",
        "S37","S38","S39","S40","S41","S42","S43","S44","S45","S46","S47","S48","S49","S50","S51","S52","S53","S54",
        "S55","S56","S57","S58","S59","S60","S61","S62","S63","S64","S65","S66","S67","S68","S69","T30","T31","T32",
        "T33","T34","T35","T36","T37","T38","T39","T40","T41","T42","T43","T44","T45","T46","T47","T48","T49","T50",
        "T51","T52","T53","T54","T55","T56","T57","T58","T59","T60","T61","T62","T63","T64","T65","T66","T67","T68",
        "T69","U30","U31","U32","U33","U34","U35","U36","U37","U38","U39","U40","U41","U42","U43","U44","U45","U46",
        "U47","U48","U49","U50","U51","U52","U53","U54","U55","U56","U57","U58","U59","U60","U61","U62","U63","U64",
        "U65","U66","U67","U68","U69","W30","W31","W32","W33","W34","W35","W36","W37","W38","W39","W40","W41","W42",
        "W43","W44","W45","W46","W47","W48","W49","W50","W51","W52","W53","W54","W55","W56","W57","W58","W59","W60",
        "W61","W62","W63","W64","W65","W66","W67","W68","W69","X30","X31","X32","X33","X34","X35","X36","X37","X38",
        "X39","X40","X41","X42","X43","X44","X45","X46","X47","X48","X49","X50","X51","X52","X53","X54","X55","X56",
        "X57","X58","X59","X60","X61","X62","X63","X64","X65","X66","X67","X68","X69","Y30","Y31","Y32","Y33","Y34",
        "Y35","Y36","Y37","Y38","Y39","Y40","Y41","Y42","Y43","Y44","Y45","Y46","Y47","Y48","Y49","Y50","Y51","Y52",
        "Y53","Y54","Y55","Y56","Y57","Y58","Y59","Y60","Y61","Y62","Y63","Y64","Y65","Y66","Y67","Y68","Y69","Z30",
        "Z31","Z32","Z33","Z34","Z35","Z36","Z37","Z38","Z39","Z40","Z41","Z42","Z43","Z44","Z45","Z46","Z47","Z48",
        "Z49","Z50","Z51","Z52","Z53","Z54","Z55","Z56","Z57","Z58","Z59","Z60","Z61","Z62","Z63","Z64","Z65","Z66",
        "Z67","Z68","Z69")

    val icpc2Entries = icpc2KomplettSheet
        .filter { !it.getCell(0).stringCellValue.toString().matches(Regex("Kode"))  }
        .filter { !icpc2ProsesCodes.contains(it.getCell(0).stringCellValue.toString())  }
        .filter { !it.getCell(0).stringCellValue.toString().matches(Regex(""))  }
        .map {row -> Entry (
            codeValue = row.getCell(0).stringCellValue.toString(),
            text = row.getCell(2).stringCellValue.toString())
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

    log.info("Finish writing to json")

    icd10Url.disconnect()
    icpc2Url.disconnect()
}