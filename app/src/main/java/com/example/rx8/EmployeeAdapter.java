package com.example.rx8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rx8.Employee;
import com.example.rx8.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private List<Employee> employees;
    private Context context;

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.employeeNameTextView.setText(employee.getName());

        // Configurar o botão de expansão/collapsar
        holder.expandButton.setOnClickListener(v -> {
            boolean isVisible = holder.detailsLayout.getVisibility() == View.VISIBLE;
            holder.detailsLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.expandButton.setImageResource(isVisible ? R.drawable.ic_expand_more : R.drawable.ic_expand_less);
        });

        // Verificar se os detalhes devem ser exibidos ou recolhidos com base no estado
        boolean isVisible = holder.detailsLayout.getVisibility() == View.VISIBLE;
        holder.expandButton.setImageResource(isVisible ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);

        // Preencher os detalhes do funcionário (layout de detalhes)
        holder.configureDetails(employee);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeNameTextView;
        ImageButton expandButton;
        LinearLayout detailsLayout;
        TextView employeeCPFTextView;

        ViewHolder(View itemView) {
            super(itemView);
            employeeNameTextView = itemView.findViewById(R.id.employeeNameTextView);
            expandButton = itemView.findViewById(R.id.expandButton);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            employeeCPFTextView = itemView.findViewById(R.id.employeeCPFTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Item clicado por um longo período
                        showDeleteConfirmationDialog(position);
                        return true;
                    }
                    return false;
                }
            });
        }

        private void showDeleteConfirmationDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Deseja realmente excluir este funcionário?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        // Excluir o funcionário do banco de dados
                        deleteEmployee(position);
                    })
                    .setNegativeButton("Não", null)
                    .show();
        }
        private void deleteEmployee(int position) {
            // Excluir o funcionário do banco de dados
            EmployeeDatabase employeeDatabase = EmployeeDatabase.getInstance();
            employeeDatabase.deleteEmployee(employees.get(position).getId());

            // Remover o funcionário da lista
            employees.remove(position);

            // Notificar o adapter sobre a mudança nos dados
            notifyDataSetChanged();
        }


        // Método para preencher os detalhes do funcionário
        public void configureDetails(Employee employee) {
            TextView nameTextView = itemView.findViewById(R.id.employeeNameTextView);
            TextView cpfTextView = itemView.findViewById(R.id.employeeCPFTextView);
            TextView admissionDateTextView = itemView.findViewById(R.id.employeeAdmissionDateTextView);
            TextView positionTextView = itemView.findViewById(R.id.employeePositionTextView);
            TextView salaryTextView = itemView.findViewById(R.id.employeeSalaryTextView);
            TextView paymentStatusTextView = itemView.findViewById(R.id.employeePaymentStatusTextView);
            TextView paymentStatusTextView1 = itemView.findViewById(R.id.employeePaymentStatusTextView1);
            TextView codigoChaveTextView = itemView.findViewById(R.id.employeeCodigoChaveTextView);

            nameTextView.setText(employee.getName());
            String maskedCpf = maskCpf(employee.getCpf());
            employeeCPFTextView.setText("CPF: " + maskedCpf);
            admissionDateTextView.setText("Data de Admissão: " + employee.getDateOfAdmission());
            codigoChaveTextView.setText("Código-Chave: " + employee.getId());
            positionTextView.setText("Cargo: " + employee.getPosition());
            salaryTextView.setText("Salário: R$ " + employee.getSalary());
            paymentStatusTextView.setText(employee.getPaymentStatus());
            paymentStatusTextView1.setText("Status do pagamento: " + employee.getPaymentStatus());
        }

        private String maskCpf(String cpf) {
            if(cpf != null && cpf.length() == 11) {
                return cpf.substring(0, 3) + ".***.***-"+cpf.substring(9);
            }
            return cpf;
        }
    }
}
