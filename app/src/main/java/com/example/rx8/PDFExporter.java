package com.example.rx8;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PDFExporter {
    public static void exportarRelatorio(Context context) {
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String dataHoraAtual = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String nomeArquivo = "Relatorio_" + dataHoraAtual + ".pdf";
        String pdfFilePath = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pdfFilePath = Paths.get(downloadPath, nomeArquivo).toString();
        }


        try {
            PdfWriter writer = new PdfWriter(pdfFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setTextAlignment(TextAlignment.LEFT);
            document.setHorizontalAlignment(HorizontalAlignment.LEFT);

            // Adicione o cabeçalho
            document.add(new Paragraph("RX8Pay").setBold());
            document.add(new Paragraph("Data de Emissão: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date())));

            // Adicione o título
            document.add(new Paragraph("Relatório de Despesas").setBold());

            // Adicione os dados em forma de tabela
            Table table = new Table(2);
            table.addCell("Valor da cota mensal:");
            table.addCell(formatarValor(ConsultarCotaMensal()));

            table.addCell("Total gasto com salários (pago):");
            table.addCell(formatarValor(totalSalariosPagos()));

            table.addCell("Total gasto com salários (novo):");
            table.addCell(formatarValor(totalSalariosNovos()));

            table.addCell("Total gasto com benefícios (pago):");
            table.addCell(formatarValor(totalBeneficios()));

            table.addCell("Total gastos/descontos da empresa:");
            table.addCell(formatarValor(gastosComunsTotais()));

            table.addCell("Valor restante da cota mensal:");
            table.addCell(formatarValor(cotaMensal().subtract(gastosComunsTotais())));

            document.add(table);

            // Adicione a porcentagem de aumento
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("É esperado um aumento de " + porcentagemSalariosNovos() + "% em relação aos gastos anteriores.").setFontSize(12));

            document.close();
            mostrarToast(context, "Relatório exportado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarToast(Context context, String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    private static BigDecimal ConsultarCotaMensal() {
        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String cotaQuery = "SELECT Valor FROM cotamensal";
            try (PreparedStatement cotaStatement = connection.prepareStatement(cotaQuery);
                 ResultSet cotaResult = cotaStatement.executeQuery()) {
                if (cotaResult.next()) {
                    return cotaResult.getBigDecimal("Valor");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    private static BigDecimal totalSalariosPagos() {
        BigDecimal totalSalariosPagos = BigDecimal.ZERO;

        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String query = "SELECT Salario FROM tbl_funcionario WHERE StatusPagamento = 'pago'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BigDecimal salario = resultSet.getBigDecimal("Salario");
                    totalSalariosPagos = totalSalariosPagos.add(salario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSalariosPagos;
    }

    private static BigDecimal totalSalariosNovos() {
        BigDecimal totalSalariosNovos = BigDecimal.ZERO;

        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String query = "SELECT Salario FROM tbl_funcionario WHERE StatusPagamento = 'novo'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BigDecimal salario = resultSet.getBigDecimal("Salario");
                    totalSalariosNovos = totalSalariosNovos.add(salario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSalariosNovos;
    }

    private static BigDecimal totalBeneficios() {
        BigDecimal totalBeneficios = BigDecimal.ZERO;

        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String query = "SELECT Salario FROM tbl_funcionario WHERE StatusPagamento = 'pago'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BigDecimal salario = resultSet.getBigDecimal("Salario");
                    BigDecimal beneficios = calcularBeneficios(salario);
                    totalBeneficios = totalBeneficios.add(beneficios);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalBeneficios;
    }

    private static String formatarValor(BigDecimal valor) {
        return String.format(Locale.getDefault(), "%.2f", valor);
    }

    private static BigDecimal gastosComunsTotais() {
        BigDecimal gastosComunsTotais = BigDecimal.ZERO;

        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String query = "SELECT Salario FROM tbl_funcionario";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BigDecimal salario = resultSet.getBigDecimal("Salario");
                    BigDecimal gastosComuns = calcularGastosComuns(salario);
                    gastosComunsTotais = gastosComunsTotais.add(gastosComuns);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gastosComunsTotais;
    }

    private static BigDecimal cotaMensal() {
        BigDecimal cotaMensal = BigDecimal.ZERO;

        try (Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC", "fishitest", "luvinhas23")) {
            String query = "SELECT Valor FROM cotamensal";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cotaMensal = resultSet.getBigDecimal("Valor");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cotaMensal;
    }

    private static BigDecimal porcentagemSalariosNovos() {
        return totalSalariosNovos()
                .divide(totalSalariosPagos().add(totalSalariosNovos()), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }

    private static BigDecimal calcularBeneficios(BigDecimal salario) {
        BigDecimal dsr = salario.multiply(new BigDecimal("0.05"));
        BigDecimal valeTransporte = salario.multiply(new BigDecimal("0.06"));
        BigDecimal valeRefeicao = salario.multiply(new BigDecimal("0.08"));
        BigDecimal seguroDeVida = new BigDecimal("150.00");
        return dsr.add(valeTransporte).add(valeRefeicao).add(seguroDeVida);
    }

    private static BigDecimal calcularGastosComuns(BigDecimal salario) {
        BigDecimal percentualFGTS = salario.multiply(new BigDecimal("0.08"));
        return percentualFGTS;
    }

}
