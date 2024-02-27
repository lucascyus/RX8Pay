package com.example.rx8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class FormCadastroFuncionario extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PopupWindow popupWindow;
    private Spinner spinnerCargo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_funcionarios);

        Button btnCadastrar = findViewById(R.id.bt_Cadastrar);

        // Definir um listener de clique para o botão
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obter o código aleatório
                String codigo = gerarCodigoAleatorio(5); // ou o tamanho desejado

                // Chamar o método para inserir no banco de dados
                inserirCodigoNoBancoDeDados(codigo);
            }
        });

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

        EditText editTextSalario = findViewById(R.id.edit_Salario);

        spinnerCargo = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opcoes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCargo.setAdapter(adapter);

        spinnerCargo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String opcaoSelecionada = parentView.getItemAtPosition(position).toString();
                double salario = obterSalarioPorCargo(opcaoSelecionada);
                editTextSalario.setText(String.valueOf(salario));
                // Faça algo com a opção selecionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ação quando nada é selecionado (opcional)
            }
        });
    }

    private void inserirCodigoNoBancoDeDados(String codigo) {
        String escolha = spinnerCargo.getSelectedItem().toString();
        double salario = obterSalarioPorCargo(escolha);

        EditText editTextNome = findViewById(R.id.edit_Nome);
        String nome = editTextNome.getText().toString();

        EditText editTextDataNascimento = findViewById(R.id.edit_DataNascimento);
        String dataNascimentoString = editTextDataNascimento.getText().toString();

        EditText editTextCpf = findViewById(R.id.edit_Cpf);
        String cpf = editTextCpf.getText().toString();

        EditText editTextRg = findViewById(R.id.edit_Rg);
        String rg = editTextRg.getText().toString();

        EditText editTextSexo = findViewById(R.id.edit_Sexo);
        String sexo = editTextSexo.getText().toString();

        EditText editTextCtps = findViewById(R.id.edit_Ctps);
        String ctps = editTextCtps.getText().toString();

        EditText editTextTituloEleitor = findViewById(R.id.edit_TituloEleitor);
        String tituloEleitor = editTextTituloEleitor.getText().toString();

        EditText editTextReservista = findViewById(R.id.edit_Reservista);
        String reservista = editTextReservista.getText().toString();

        EditText editTextTelefone = findViewById(R.id.edit_Telefone);
        String telefone = editTextTelefone.getText().toString();

        EditText editTextEmail = findViewById(R.id.edit_Email);
        String email = editTextEmail.getText().toString();

        EditText editTextCep = findViewById(R.id.edit_Cep);
        String cep = editTextCep.getText().toString();

        EditText editTextEndereco = findViewById(R.id.edit_Endereco);
        String endereco = editTextEndereco.getText().toString();

        EditText editTextNomeBanco = findViewById(R.id.edit_NomeBanco);
        String nomeBanco = editTextNomeBanco.getText().toString();

        EditText editTextNumeroConta = findViewById(R.id.edit_NumeroConta);
        String numeroConta = editTextNumeroConta.getText().toString();

        EditText editTextNumeroBanco = findViewById(R.id.edit_NumeroBanco);
        String numeroBanco = editTextNumeroBanco.getText().toString();

        EditText editTextAgenciaBancaria = findViewById(R.id.edit_AgenciaBancaria);
        String agenciaBancaria = editTextAgenciaBancaria.getText().toString();

        EditText editTextTipoConta = findViewById(R.id.edit_TipoConta);
        String tipoConta = editTextTipoConta.getText().toString();

        EditText editTextDataAdmissao = findViewById(R.id.edit_DataAdmissao);
        String dataAdmissaoString = editTextDataAdmissao.getText().toString();

        String dataNascimentoFormatada = converterDataParaFormatoBanco(dataNascimentoString);
        String dataAdmissaoFormatada = converterDataParaFormatoBanco(dataAdmissaoString);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date dataAdmissao = sdf.parse(editTextDataAdmissao.getText().toString());
            Date proximoDia5 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(proximoDia5);
            calendar.set(Calendar.DAY_OF_MONTH, 5);
            proximoDia5 = calendar.getTime();

            long diferenca = proximoDia5.getTime() - dataAdmissao.getTime();
            int diasAteProximoDia5 = (int) (diferenca / (24 * 60 * 60 * 1000));

            String statusPagamento;

            if (diasAteProximoDia5 <= 0) {
                // O funcionário se cadastrou no dia 5 ou depois.
                // Defina o status como "Novo".
                statusPagamento = "Novo";
            } else {
                // Mais de 15 dias até o próximo dia 5.
                // Defina o status como "Não pago".
                statusPagamento = "Não pago";
            }

            // Substitua pelo método real para obter o nome do usuário
            try (Connection connection = ConnectionPagamentos.connect()) {
                String query1 = "INSERT INTO tbl_funcionario (Id, Nome, DataNascimento, Cpf, Rg, Sexo, Ctps, TituloEleitor, Reservista, Telefone, Email, Endereco, Cargo, Salario, DataAdmissao, Cep, statusPagamento) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query1)) {
                    preparedStatement.setString(1, codigo);
                    preparedStatement.setString(2, nome);  // Substitua pelo método real para obter o nome do usuário
                    preparedStatement.setDate(3, java.sql.Date.valueOf(dataNascimentoFormatada));
                    preparedStatement.setString(4, cpf);
                    preparedStatement.setString(5, rg);
                    preparedStatement.setString(6, sexo);
                    preparedStatement.setString(7, ctps);
                    preparedStatement.setString(8, tituloEleitor);
                    preparedStatement.setString(9, reservista);
                    preparedStatement.setString(10, telefone);
                    preparedStatement.setString(11, email);
                    preparedStatement.setString(12, endereco);
                    preparedStatement.setString(13, escolha);
                    preparedStatement.setString(14, String.valueOf(salario));
                    preparedStatement.setDate(15, java.sql.Date.valueOf(dataAdmissaoFormatada));
                    preparedStatement.setString(16, cep);
                    preparedStatement.setString(17, statusPagamento);

                    preparedStatement.executeUpdate();

                }
                String query2 = "INSERT INTO tbl_banco (id, Nome, NumeroConta, NumeroBanco, AgenciaBancaria, TipoConta) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                    preparedStatement2.setString(1, codigo);
                    preparedStatement2.setString(2, nomeBanco);
                    preparedStatement2.setString(3, numeroConta);
                    preparedStatement2.setString(4, numeroBanco);
                    preparedStatement2.setString(5, agenciaBancaria);
                    preparedStatement2.setString(6, tipoConta);

                    preparedStatement2.executeUpdate();
                    Toast.makeText(FormCadastroFuncionario.this, "Funcionário Cadastrado!", Toast.LENGTH_SHORT).show();
                }
                // ... restante do código ...
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // ... seu código posterior ...
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String converterDataParaFormatoBanco(String data) {
        // Suponha que a data esteja no formato "dd/mm/aaaa"
        // Divida a string em dia, mês e ano
        String[] partes = data.split("/");
        if (partes.length == 3) {
            // Reorganize as partes para o formato "aaaa-mm-dd"
            return partes[2] + "-" + partes[1] + "-" + partes[0];
        } else {
            // Trate o caso em que a string não está no formato esperado
            // Pode lançar uma exceção ou retornar um valor padrão, dependendo do seu caso
            return null;
        }
    }

    private String gerarCodigoAleatorio(int tamanho) {
        final String caracteresPermitidos = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        char[] codigo = new char[tamanho];

        for (int i = 0; i < tamanho; i++) {
            int indice = random.nextInt(caracteresPermitidos.length());
            codigo[i] = caracteresPermitidos.charAt(indice);
        }

        return new String(codigo);
    }

    private boolean codigoExisteNoBancoDeDados(String codigo) {
        try (Connection connection = ConnectionPagamentos.connect()) {
            String query = "SELECT COUNT(*) FROM tbl_funcionario WHERE Id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, codigo);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void CadastroPage(View view) {
        // Criar um Intent para iniciar a nova atividade
        Intent intent = new Intent(this, FormCadastroFuncionario.class);

        // Iniciar a nova atividade
        startActivity(intent);
    }

    // Função para verificar se o CPF já está vinculado a algum funcionário
    private boolean cpfVinculado(String cpf) {
        try (Connection connection = ConnectionPagamentos.connect()) {
            String query = "SELECT COUNT(*) FROM tbl_funcionario WHERE Cpf = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, cpf);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Função para verificar se o RG já está vinculado a algum funcionário
    private boolean rgVinculado(String rg) {
        try (Connection connection = ConnectionPagamentos.connect()) {
            String query = "SELECT COUNT(*) FROM tbl_funcionario WHERE Rg = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, rg);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Função para verificar se a CTPS já está vinculada a algum funcionário
    private boolean ctpsVinculado(String ctps) {
        try (Connection connection = ConnectionPagamentos.connect()) {
            String query = "SELECT COUNT(*) FROM tbl_funcionario WHERE Ctps = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, ctps);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Função para verificar se o título de eleitor já está vinculado a algum funcionário
    private boolean tituloVinculado(String tituloEleitor) {
        try (Connection connection = ConnectionPagamentos.connect()) {
            String query = "SELECT COUNT(*) FROM tbl_funcionario WHERE TituloEleitor = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, tituloEleitor);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double obterSalarioPorCargo(String cargo) {
        switch (cargo) {
            case "Assistente Administrativo":
                return 2560.30;
            case "Assistente de Vendas":
                return 1520.60;
            case "Suporte ao Cliente":
                return 2100.50;
            case "Auxiliar de Estoque":
                return 1340.12;
            case "Recepcionista":
                return 1403.00;
            case "Assistente de Marketing":
                return 1660.90;
            case "Auxiliar de Contabilidade":
                return 2433.21;
            case "Operador de Caixa":
                return 1413.00;
            case "Assitente de Recursos Humanos":
                return 1890.06;
            case "Auxiliar de Limpeza":
                return 1320.00;
            default:
                return 0.00;
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

    // Método fictício para obter o nome do usuário
    private String getNomeDoUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("suasPreferencias", MODE_PRIVATE);
        return sharedPreferences.getString("nomeUsuario", "Not Found");
    }

    // Método fictício para realizar o logout
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

