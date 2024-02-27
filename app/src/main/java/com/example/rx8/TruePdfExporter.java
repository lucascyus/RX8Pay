package com.example.rx8;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TruePdfExporter {

    public static void pdfExportar(Context context) {
        String dbUrl = "jdbc:jtds:sqlserver://192.168.0.15:1433/SALARY_SYNC";
        String dbUser = "fishitest";
        String dbPassword = "luvinhas23";

        List<String> pdfFiles = new ArrayList<>();

        try {
            // Criar diretório para os PDFs
            String pdfFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            pdfFolderPath = pdfFolderPath + File.separator + "PDFs"; // Caminho para PDFs
            File pdfFolder = new File(pdfFolderPath);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }

            // Conectar ao banco de dados
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

                // Consultar dados
                String query = "SELECT Nome, Cargo, Salario FROM tbl_funcionario";
                try (PreparedStatement statement = connection.prepareStatement(query);
                     ResultSet reader = statement.executeQuery()) {

                    while (reader.next()) {
                        String nomeFuncionario = reader.getString("Nome");
                        String cargoFuncionario = reader.getString("Cargo");
                        double salario = reader.getDouble("Salario");

                        // Criar documento PDF
                        Document document = new Document();
                        String pdfFilePath = pdfFolderPath + File.separator + nomeFuncionario + "_Demonstrativo.pdf";
                        pdfFiles.add(pdfFilePath);
                        PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
                        document.open();

                        // Adicionar cabeçalho
                        PdfPTable headerTable = new PdfPTable(1);
                        PdfPCell headerCell = new PdfPCell(new Paragraph("Demonstrativo de Pagamento", new Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)));
                        headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                        headerTable.addCell(headerCell);
                        document.add(headerTable);

                        // Adicionar informações do funcionário
                        Paragraph nomeParagrafo = new Paragraph();
                        nomeParagrafo.setAlignment(Paragraph.ALIGN_CENTER);
                        nomeParagrafo.add(new Paragraph(nomeFuncionario, new Font(Font.FontFamily.HELVETICA, 12f)));
                        document.add(nomeParagrafo);

                        Paragraph cargoParagrafo = new Paragraph();
                        cargoParagrafo.setAlignment(Paragraph.ALIGN_CENTER);
                        cargoParagrafo.add(new Paragraph(cargoFuncionario, new Font(Font.FontFamily.HELVETICA, 12f)));
                        document.add(cargoParagrafo);

                        document.add(new Paragraph(" "));

                        // Adicionar cálculos de benefícios
                        double dsr = salario * 0.05;
                        double valeTransporte = salario * 0.06;
                        double valeRefeicao = salario * 0.08;
                        double seguroDeVida = 150;

                        double inss = 0;
                        if (salario <= 1302.00) {
                            inss = salario * 0.075;
                        } else if (salario <= 2571.29) {
                            inss = salario * 0.09;
                        } else if (salario <= 3856.94) {
                            inss = salario * 0.12;
                        } else if (salario <= 7507.49) {
                            inss = salario * 0.14;
                        }
                        // Lida com salários acima de 7507.49, se necessário.

                        double percentualFGTS = salario * 0.08;
                        double baseFGTS = salario;
                        double baseCalculoIRRF = salario - inss;

                        dsr = formatarParaDuasCasasDecimais(dsr);
                        valeTransporte = formatarParaDuasCasasDecimais(valeTransporte);
                        valeRefeicao = formatarParaDuasCasasDecimais(valeRefeicao);
                        seguroDeVida = formatarParaDuasCasasDecimais(seguroDeVida);
                        inss = formatarParaDuasCasasDecimais(inss);
                        percentualFGTS = formatarParaDuasCasasDecimais(percentualFGTS);
                        baseFGTS = formatarParaDuasCasasDecimais(baseFGTS);
                        baseCalculoIRRF = formatarParaDuasCasasDecimais(baseCalculoIRRF);

                        // Adicionar tabela de benefícios
                        PdfPTable beneficiosTable = new PdfPTable(2);
                        beneficiosTable.addCell(new PdfPCell(new Paragraph("Descrição", new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))));
                        beneficiosTable.addCell(new PdfPCell(new Paragraph("Valor (R$)", new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))));
                        beneficiosTable.addCell("DSR");
                        beneficiosTable.addCell(Double.toString(dsr));
                        beneficiosTable.addCell("Vale Transporte");
                        beneficiosTable.addCell(Double.toString(valeTransporte));
                        beneficiosTable.addCell("Vale Refeição");
                        beneficiosTable.addCell(Double.toString(valeRefeicao));
                        beneficiosTable.addCell("Seguro de Vida");
                        beneficiosTable.addCell(Double.toString(seguroDeVida));
                        beneficiosTable.addCell("INSS Folha");
                        beneficiosTable.addCell(Double.toString(-inss));

                        document.add(beneficiosTable);

                        // Adicionar dados
                        PdfPTable dadosTable = new PdfPTable(2);
                        dadosTable.addCell("Salário Base");
                        dadosTable.addCell(Double.toString(salario));
                        dadosTable.addCell("Salário Contribuinte INSS");
                        dadosTable.addCell(Double.toString(inss));
                        dadosTable.addCell("Base Cálculo FGTS");
                        dadosTable.addCell(Double.toString(baseFGTS));
                        dadosTable.addCell("FGTS do Mês");
                        dadosTable.addCell(Double.toString(percentualFGTS));
                        dadosTable.addCell("Base Cálculo IRRF");
                        dadosTable.addCell(Double.toString(baseCalculoIRRF));

                        document.add(dadosTable);

                        document.add(new Paragraph(" "));

                        // Adicionar total de benefícios
                        double totalBeneficios = dsr + valeTransporte + valeRefeicao + seguroDeVida;
                        totalBeneficios = formatarParaDuasCasasDecimais(totalBeneficios);

                        // Adicionar o valor total de benefícios no PDF
                        Paragraph totalBeneficiosParagrafo = new Paragraph();
                        totalBeneficiosParagrafo.setAlignment(Paragraph.ALIGN_CENTER);
                        totalBeneficiosParagrafo.add(new Paragraph("Total de Benefícios: " + totalBeneficios, new Font(Font.FontFamily.HELVETICA, 12f)));
                        document.add(totalBeneficiosParagrafo);

                        // Salário Líquido
                        double salarioLiquido = salario - inss;
                        salarioLiquido = formatarParaDuasCasasDecimais(salarioLiquido);
                        Paragraph liquidoSalario = new Paragraph();
                        liquidoSalario.setAlignment(Paragraph.ALIGN_CENTER);
                        liquidoSalario.add(new Paragraph("Salário Líquido: " + salarioLiquido, new Font(Font.FontFamily.HELVETICA, 12f)));
                        document.add(liquidoSalario);

                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));

                        // Espaço para preencher com caneta
                        PdfPTable assinaturaTable = new PdfPTable(1);
                        PdfPCell cell = new PdfPCell(new Paragraph("Assinatura do Funcionário: _______________________________"));
                        cell.setBorder(0);
                        assinaturaTable.addCell(cell);

                        document.add(assinaturaTable);

                        document.close();
                    }

                    int zipFileNumber = 1;
                    String zipFilePath = pdfFolderPath + File.separator + "funcionarios_" + zipFileNumber + ".zip";
                    while (new File(zipFilePath).exists()) {
                        zipFileNumber++;
                        zipFilePath = pdfFolderPath + File.separator + "funcionarios_" + zipFileNumber + ".zip";
                    }

                    File zipFile = new File(zipFilePath);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

                    for (String pdfFile : pdfFiles) {
                        File file = new File(pdfFile);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOutputStream.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(bytes)) >= 0) {
                            zipOutputStream.write(bytes, 0, length);
                        }

                        fileInputStream.close();
                    }

                    zipOutputStream.close();

                    // Mostrar toast indicando sucesso
                    showToast(context, "PDFs exportados com sucesso!");

                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double formatarParaDuasCasasDecimais(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}