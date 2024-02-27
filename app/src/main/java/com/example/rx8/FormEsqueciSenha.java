package com.example.rx8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormEsqueciSenha extends AppCompatActivity {
    Connection connect;
    String ConnectionResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_senha);

        getSupportActionBar().hide();

        TextView jaSeiSenha = findViewById(R.id.link_login);

        jaSeiSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormEsqueciSenha.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button buttonRedefinirSenha = findViewById(R.id.bt_redefinir_senha);

        buttonRedefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Connection connection = ConnectionSql.connect();

                if(connection != null) {
                    EditText loginEditText = findViewById(R.id.edit_login);
                    EditText emailEditText = findViewById(R.id.edit_email);
                    EditText senhaEditText = findViewById(R.id.edit_senha);

                    String username = loginEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = senhaEditText.getText().toString();

                    if (isContaMatch(connection, username, email)) {
                        boolean updated = updatedPassword(connection, username, password);

                        if (updated) {
                            Toast.makeText(FormEsqueciSenha.this, "Senha redefinida", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(FormEsqueciSenha.this, "Erro ao redefinir", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FormEsqueciSenha.this, "Login e Email não correspondem", Toast.LENGTH_SHORT).show();
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

    private boolean isContaMatch(Connection connection, String username, String email) {
        String query = "select * from tbl_login where username = ? and email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean updatedPassword(Connection connection, String username, String password) {
        String query = "update tbl_login set password = ? where username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
