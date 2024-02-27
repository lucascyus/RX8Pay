package com.example.rx8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmployeeDatabase {

    private static EmployeeDatabase instance;
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();

        // Aqui, você deve escrever o código real para consultar o banco de dados e preencher a lista.
        // Substitua este exemplo fictício pelo seu código de consulta ao banco de dados.
        // Exemplo fictício:
        Connection connection = ConnectionPagamentos.connect();
        String query = "SELECT * FROM tbl_funcionario";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Preencha os detalhes do funcionário a partir do ResultSet
                String name = resultSet.getString("Nome");
                String cpf = resultSet.getString("cpf");
                String dateOfAdmission = resultSet.getString("DataAdmissao");
                String position = resultSet.getString("Cargo");
                double salary = resultSet.getDouble("Salario");
                String paymentStatus = resultSet.getString("statusPagamento");
                String id = resultSet.getString("id");

                int currentDayOfMonth = getCurrentDayOfMonth();
                if (currentDayOfMonth >= 1 && currentDayOfMonth <= 4 && !"Novo".equals(paymentStatus)) {
                    paymentStatus = "Pendente";
                    updatePaymentStatus(id, paymentStatus);
                } else if (currentDayOfMonth >= 5 && "Pendente".equals(paymentStatus)) {
                    paymentStatus = "Pago";
                    updatePaymentStatus(id, paymentStatus);
                }

                // Crie um objeto Employee com os detalhes e adicione-o à lista
                Employee employee = new Employee(name, cpf, dateOfAdmission, position, salary, paymentStatus, id);
                employees.add(employee);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    private EmployeeDatabase() {
    }

    // Método para obter a instância única da classe (implementação do Singleton)
    public static synchronized EmployeeDatabase getInstance() {
        if (instance == null) {
            instance = new EmployeeDatabase();
        }
        return instance;
    }

    private int getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void deleteEmployee(String employeeId) {
        Connection connection = ConnectionPagamentos.connect();
        String query = "DELETE FROM tbl_funcionario WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employeeId);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    } //Deletar Funcionario

    public void updatePaymentStatus(String employeeId, String newStatus) {
        Connection connection = ConnectionPagamentos.connect();
        String query = "UPDATE tbl_funcionario SET statusPagamento = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, employeeId);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
