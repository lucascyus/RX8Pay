package com.example.rx8;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormPagamentos extends AppCompatActivity {
    private PopupWindow popupWindow;
    final static int REQUEST_CODE = 1232;


    private List<Employee> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pagamentos);

        askPermission();

        // conexão com o banco de dados e lista de funcionários
        EmployeeDatabase employeeDatabase = EmployeeDatabase.getInstance();
        List<Employee> employees = employeeDatabase.getEmployees();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EmployeeAdapter adapter = new EmployeeAdapter(this, employees);
        recyclerView.setAdapter(adapter);

        // Personalizar a ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo)));
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_user);

        // Configurar o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.nav_pagamentos) {
                // Abrir a página de pagamentos
                intent = new Intent(this, FormPagamentos.class);
            } else if (item.getItemId() == R.id.nav_cadastro_funcionarios) {
                // Abrir a página de cadastro de funcionários
                intent = new Intent(this, FormCadastroFuncionario.class);
            }

            if (intent != null) {
                startActivity(intent);
                return true;
            }

            return false;
        });

        Button exportButton = findViewById(R.id.bt_GerarRelatorio);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFExporter.exportarRelatorio(FormPagamentos.this);
            }
        });

        Button exportButtonPdf = findViewById(R.id.bt_ExportarPdf);
        exportButtonPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TruePdfExporter.pdfExportar(FormPagamentos.this);
            }
        });
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    private void createPdf() {
        Document document = new Document();

        try {
            // Defina o caminho do arquivo PDF na pasta Downloads
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Relatorio_" + timeStamp + ".pdf";
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(folder, fileName);

            // Crie o arquivo PDF
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.open();

            // Adicione o conteúdo ao PDF (por exemplo, informações dos funcionários)
            List<Employee> employees = getEmployees();
            for (Employee employee : employees) {
                document.add(new Paragraph("Nome: " + employee.getName() + ", Salário: " + employee.getSalary()));
            }

            Toast.makeText(this, "Relatório PDF gerado com sucesso: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar o relatório PDF", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verifique se o ícone de usuário foi clicado
        if (item.getItemId() == android.R.id.home) {
            // Inflate do layout do popup
            View popupView = LayoutInflater.from(this).inflate(R.layout.user_popup, null);

            // Configuração do AlertDialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(popupView);
            alertDialogBuilder.setCancelable(true);

            // Configuração dos elementos dentro do popup
            TextView userNameTextView = popupView.findViewById(R.id.userNameTextView);
            Button logoutButton = popupView.findViewById(R.id.logoutButton);

            // Substitua "ObtenhaNomeDoUsuario()" pelo método real que obtém o nome do usuário logado
            userNameTextView.setText("@" + getNomeDoUsuario());

            // Defina o comportamento do botão de logout
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Adicione o código para realizar o logout aqui
                    // Por exemplo, chame um método que limpe as credenciais do usuário
                    // e redirecione para a tela de login
                    realizarLogout();
                }
            });

            // Crie e exiba o AlertDialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    // obter o nome do usuário
    private List<Employee> getEmployees() {
        // lista de funcionários do banco de dados
        return EmployeeDatabase.getInstance().getEmployees();
    }

    private String getNomeDoUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("suasPreferencias", MODE_PRIVATE);
        return sharedPreferences.getString("nomeUsuario", "Nome Padrão caso não encontre");
    }

    // realizar o logout
    private void realizarLogout() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Certifique-se de dispensar o PopupWindow para evitar vazamentos de memória
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }


}
