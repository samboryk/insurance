
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainApp extends Application {

    private final BranchDAO branchDAO = new BranchDAO();
    private final InsuranceTypeDAO typeDAO = new InsuranceTypeDAO();
    private final ContractDAO contractDAO = new ContractDAO();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createBranchTab(),
                createTypeTab(),
                createContractTab(),
                createReportTab()
        );

        Scene scene = new Scene(tabPane, 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Страхова компанія");
        stage.setScene(scene);
        stage.show();
    }

    private Tab createBranchTab() {
        Tab tab = new Tab("Філії");
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        TableView<Branch> table = new TableView<>();
        TableColumn<Branch, String> colName = new TableColumn<>("Назва");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        TableColumn<Branch, String> colAddress = new TableColumn<>("Адреса");
        colAddress.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        TableColumn<Branch, String> colPhone = new TableColumn<>("Телефон");
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        table.getColumns().addAll(colName, colAddress, colPhone);
        table.getItems().addAll(branchDAO.findAll());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        TextField tfName = new TextField();
        TextField tfAddress = new TextField();
        TextField tfPhone = new TextField();
        form.addRow(0, new Label("Назва філії:"), tfName);
        form.addRow(1, new Label("Адреса:"), tfAddress);
        form.addRow(2, new Label("Телефон:"), tfPhone);

        Button btnAdd = new Button("Додати філію");
        btnAdd.setOnAction(e -> {
            if (!tfName.getText().trim().isEmpty()) {
                Branch b = new Branch(tfName.getText().trim(), tfAddress.getText().trim(), tfPhone.getText().trim());
                branchDAO.save(b);
                table.getItems().add(b);
                tfName.clear(); tfAddress.clear(); tfPhone.clear();
            }
        });

        Button btnDelete = new Button("Видалити вибрану");
        btnDelete.setOnAction(e -> {
            Branch selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                branchDAO.delete(selected.getId());
                table.getItems().remove(selected);
            }
        });

        HBox buttons = new HBox(15, btnAdd, btnDelete);
        root.getChildren().addAll(new Label("Список філій"), table, form, buttons);
        tab.setContent(root);
        return tab;
    }

    private Tab createTypeTab() {
        Tab tab = new Tab("Види страхування");
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        TableView<InsuranceType> table = new TableView<>();
        TableColumn<InsuranceType, String> colName = new TableColumn<>("Назва виду");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        table.getColumns().add(colName);
        table.getItems().addAll(typeDAO.findAll());

        TextField tfName = new TextField();
        Button btnAdd = new Button("Додати вид");
        btnAdd.setOnAction(e -> {
            if (!tfName.getText().trim().isEmpty()) {
                InsuranceType t = new InsuranceType(tfName.getText().trim());
                typeDAO.save(t);
                table.getItems().add(t);
                tfName.clear();
            }
        });

        Button btnDelete = new Button("Видалити вибраний");
        btnDelete.setOnAction(e -> {
            InsuranceType selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                typeDAO.delete(selected.getId());
                table.getItems().remove(selected);
            }
        });

        HBox buttons = new HBox(15, btnAdd, btnDelete);
        root.getChildren().addAll(new Label("Види страхування"), table, new Label("Назва:"), tfName, buttons);
        tab.setContent(root);
        return tab;
    }

    private Tab createContractTab() {
        Tab tab = new Tab("Договори");
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        TableView<Contract> table = new TableView<>();

        TableColumn<Contract, String> colNum = new TableColumn<>("Номер договору");
        colNum.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContractNumber()));

        TableColumn<Contract, String> colDate = new TableColumn<>("Дата");
        colDate.setCellValueFactory(data -> {
            Date d = data.getValue().getConclusionDate();
            return new javafx.beans.property.SimpleStringProperty(d == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(d));
        });

        TableColumn<Contract, String> colSum = new TableColumn<>("Страхова сума");
        colSum.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", data.getValue().getInsuranceSum())));

        TableColumn<Contract, String> colRate = new TableColumn<>("Тариф");
        colRate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.4f", data.getValue().getTariffRate())));

        TableColumn<Contract, String> colPremium = new TableColumn<>("Премія");
        colPremium.setCellValueFactory(data -> {
            double premium = data.getValue().getInsuranceSum() * data.getValue().getTariffRate();
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", premium));
        });

        TableColumn<Contract, String> colBranch = new TableColumn<>("Філія");
        colBranch.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBranch().getName()));

        TableColumn<Contract, String> colType = new TableColumn<>("Вид страхування");
        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getInsuranceType().getName()));

        table.getColumns().addAll(colNum, colDate, colSum, colRate, colPremium, colBranch, colType);
        table.getItems().addAll(contractDAO.findAll());

        // Форма
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField tfNumber = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField tfSum = new TextField();
        TextField tfRate = new TextField();

        ComboBox<Branch> cbBranch = new ComboBox<>();
        cbBranch.getItems().addAll(branchDAO.findAll());

        ComboBox<InsuranceType> cbType = new ComboBox<>();
        cbType.getItems().addAll(typeDAO.findAll());

        form.addRow(0, new Label("Номер договору:"), tfNumber);
        form.addRow(1, new Label("Дата укладення:"), datePicker);
        form.addRow(2, new Label("Страхова сума:"), tfSum);
        form.addRow(3, new Label("Тарифна ставка:"), tfRate);
        form.addRow(4, new Label("Філія:"), cbBranch);
        form.addRow(5, new Label("Вид страхування:"), cbType);

        Button btnAdd = new Button("Укласти договір");
        btnAdd.setOnAction(e -> {
            if (tfNumber.getText().trim().isEmpty() || datePicker.getValue() == null ||
                    cbBranch.getValue() == null || cbType.getValue() == null) {
                return;
            }

            try {
                Contract c = new Contract();
                c.setContractNumber(tfNumber.getText().trim());
                c.setConclusionDate(java.sql.Date.valueOf(datePicker.getValue()));
                c.setInsuranceSum(Double.parseDouble(tfSum.getText().trim()));
                c.setTariffRate(Double.parseDouble(tfRate.getText().trim()));
                c.setBranch(cbBranch.getValue());
                c.setInsuranceType(cbType.getValue());

                contractDAO.save(c);
                table.getItems().add(c);

                tfNumber.clear();
                datePicker.setValue(null);
                tfSum.clear();
                tfRate.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnDelete = new Button("Видалити договір");
        btnDelete.setOnAction(e -> {
            Contract selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                contractDAO.delete(selected.getId());
                table.getItems().remove(selected);
            }
        });

        HBox buttons = new HBox(15, btnAdd, btnDelete);
        root.getChildren().addAll(new Label("Список договорів"), table, form, buttons);
        tab.setContent(root);
        return tab;
    }

    private Tab createReportTab() {
        Tab tab = new Tab("Фінансовий звіт");
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));

        Label title = new Label("Фінансова діяльність компанії");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnRefresh = new Button("Оновити звіт");
        Label totalSum = new Label("Загальна страхова сума: 0.00 грн");
        Label totalPremium = new Label("Загальна премія: 0.00 грн");

        btnRefresh.setOnAction(e -> {
            List<Contract> contracts = contractDAO.findAll();
            double sum = 0;
            double premium = 0;
            int i = 0;
            while (i < contracts.size()) {
                Contract c = contracts.get(i);
                sum = sum + c.getInsuranceSum();
                premium = premium + (c.getInsuranceSum() * c.getTariffRate());
                i = i + 1;
            }
            totalSum.setText("Загальна страхова сума: " + String.format("%.2f", sum) + " грн");
            totalPremium.setText("Загальна премія: " + String.format("%.2f", premium) + " грн");
        });

        root.getChildren().addAll(title, btnRefresh, totalSum, totalPremium);
        tab.setContent(root);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}