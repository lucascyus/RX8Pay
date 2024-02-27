package com.example.rx8;

import android.content.Intent;
import android.content.SharedPreferences;
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


public class MainActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide();


        Button buttonCadastro = findViewById(R.id.bt_criar_conta);
        EditText loginEditText = findViewById(R.id.edit_login);
        EditText passwordEditText = findViewById(R.id.edit_senha);

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FormCadastro.class);
                startActivity(intent);
            }
        }); // Formulário Criar Conta

        Button loginButton = findViewById(R.id.bt_entrar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = loginEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Connection connection = ConnectionSql.connect();

                if(connection != null) {
                    boolean authenticated = checkUserCredentials(connection, username, password);

                    if (authenticated) {
                        Toast.makeText(MainActivity.this, "Login bem sucedido", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FormPagamentos.class);
                        startActivity(intent);

                        SharedPreferences sharedPreferences = getSharedPreferences("suasPreferencias", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nomeUsuario", username);
                        editor.apply();

                    } else {
                        Toast.makeText(MainActivity.this,"Dados inválidos", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkUserCredentials(Connection connection, String username, String password) {
        String query = "select * from tbl_login where username = ? and password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void senhaPage(View view) {
        Intent intent = new Intent(this, FormEsqueciSenha.class);

        startActivity(intent);
    }
}
