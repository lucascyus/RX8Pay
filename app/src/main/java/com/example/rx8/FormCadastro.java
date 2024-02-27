package com.example.rx8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormCadastro extends AppCompatActivity {

    Connection connect;
    String ConnectionResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();

        Button buttonCriarConta = findViewById(R.id.bt_criar_conta);

        buttonCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection connection = ConnectionSql.connect();

                if (connection != null) {
                    EditText loginEditText = findViewById(R.id.edit_login);
                    EditText emailEditText = findViewById(R.id.edit_email);
                    EditText senhaEditText = findViewById(R.id.edit_senha);
                    EditText confirmaSenhaEditText = findViewById(R.id.edit_confirma_senha);

                    String username = loginEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = senhaEditText.getText().toString();
                    String confirmarSenha = confirmaSenhaEditText.getText().toString(); // Corrigido o nome da variável

                    if(UserExists(connection, username, email)) {
                        Toast.makeText(FormCadastro.this, "Email ou login já existentes", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.equals(confirmarSenha)) {
                            boolean inserted = insertUserData(connection, username, email, password);

                            if (inserted) {
                                Toast.makeText(FormCadastro.this, "Registro bem-sucedido", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FormCadastro.this, "Erro ao registrar", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FormCadastro.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                        }

                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void LoginPage(View view) {
        // Criar um Intent para iniciar a nova atividade
        Intent intent = new Intent(this, MainActivity.class);

        // Iniciar a nova atividade
        startActivity(intent);
    }

    private boolean UserExists(Connection connection, String username, String email) {
        String query = "select count(*) from tbl_login where username = ? or email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email); // Corrigido o índice do parâmetro

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean insertUserData(Connection connection, String username, String password, String email) {
        String query = "insert into tbl_login (username, email, password) values (?, ?, ?)"; // Corrigida a ordem dos campos

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email); // Corrigido o índice do parâmetro

            int rowsInserted = preparedStatement.executeUpdate();

            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
