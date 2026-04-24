package com.mycompany.lab2clucho;

import com.mycompany.lab2clucho.controller.UserControlle;
import com.mycompany.lab2clucho.controller.UserControlle.Usuario;
import com.mycompany.lab2clucho.controller.UserControlle.Rol;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MantenimientoUsuarios extends javax.swing.JDialog {

    private final UserControlle controller;
    private final Usuario usuarioActual;
    private DefaultTableModel tableModel;

    public MantenimientoUsuarios(UserControlle controller, Usuario usuarioActual) {
        this.controller    = controller;
        this.usuarioActual = usuarioActual;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("User Maintenance");
        setModal(true);
        cargarTabla();
        configurarPermisos();
    }

    private void initComponents() {

        // ── Fields ────────────────────────────────────────────────
        JLabel lblNombre = new JLabel("Name:");
        JLabel lblEmail  = new JLabel("Email:");
        JLabel lblClave  = new JLabel("Password:");
        JLabel lblRol    = new JLabel("Role:");

        txtNombre = new JTextField(20);
        txtEmail  = new JTextField(20);
        txtClave  = new JPasswordField(20);
        cmbRol    = new JComboBox<>(Rol.values());

        // ── Buttons ───────────────────────────────────────────────
        btnAgregar    = new JButton("Add");
        btnActualizar = new JButton("Update");
        btnEliminar   = new JButton("Deactivate");
        btnLimpiar    = new JButton("Clear");

        btnAgregar.addActionListener(e -> agregar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // ── Table ─────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
            new String[]{"Name", "Email", "Role", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(tableModel);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarFilaSeleccionada();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new java.awt.Dimension(500, 200));

        // ── Form panel ────────────────────────────────────────────
        JPanel panelForm = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill   = java.awt.GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(lblNombre, gbc);
        gbc.gridx = 1;                 panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(lblEmail, gbc);
        gbc.gridx = 1;                 panelForm.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(lblClave, gbc);
        gbc.gridx = 1;                 panelForm.add(txtClave, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(lblRol, gbc);
        gbc.gridx = 1;                 panelForm.add(cmbRol, gbc);

        // ── Button panel ──────────────────────────────────────────
        JPanel panelBtn = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        panelBtn.add(btnAgregar);
        panelBtn.add(btnActualizar);
        panelBtn.add(btnEliminar);
        panelBtn.add(btnLimpiar);

        // ── Current session label ─────────────────────────────────
        JLabel lblCurrentUser = new JLabel(
            "Session: " + usuarioActual.getNombre() + " [" + usuarioActual.getRol() + "]"
        );
        lblCurrentUser.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        lblCurrentUser.setForeground(java.awt.Color.DARK_GRAY);

        // ── Main layout ───────────────────────────────────────────
        JPanel panelNorte = new JPanel(new java.awt.BorderLayout(5, 5));
        panelNorte.add(lblCurrentUser, java.awt.BorderLayout.NORTH);
        panelNorte.add(panelForm,      java.awt.BorderLayout.CENTER);
        panelNorte.add(panelBtn,       java.awt.BorderLayout.SOUTH);

        setLayout(new java.awt.BorderLayout(10, 10));
        add(panelNorte, java.awt.BorderLayout.NORTH);
        add(scroll,     java.awt.BorderLayout.CENTER);

        pack();
        setSize(560, 460);
    }

    // ── Configure permissions by role ─────────────────────────────────────────
    private void configurarPermisos() {
        boolean esAdmin = usuarioActual.getRol() == Rol.ADMINISTRADOR;

        if (esAdmin) {
            // ADMIN: all fields editable
            txtNombre.setEditable(true);
            txtEmail.setEditable(true);
            txtClave.setEditable(true);
            cmbRol.setEnabled(true);
            btnAgregar.setVisible(true);
            btnEliminar.setVisible(true);
        } else {
            // USER: can only change their own password
            txtNombre.setEditable(false);
            txtEmail.setEditable(false);
            txtClave.setEditable(true);
            cmbRol.setEnabled(false);
            btnAgregar.setVisible(false);
            btnEliminar.setVisible(false);

            // Load current user data automatically
            txtNombre.setText(usuarioActual.getNombre());
            txtEmail.setText(usuarioActual.getEmail());
            cmbRol.setSelectedItem(usuarioActual.getRol());
        }

        // Email is only editable when adding a new user
        txtEmail.setEditable(esAdmin);
    }

    // ── Load selected row into fields ─────────────────────────────────────────
    private void cargarFilaSeleccionada() {
        // USER cannot select other rows
        if (usuarioActual.getRol() != Rol.ADMINISTRADOR) return;

        int row = tabla.getSelectedRow();
        txtNombre.setText((String) tableModel.getValueAt(row, 0));
        txtEmail.setText((String)  tableModel.getValueAt(row, 1));
        txtClave.setText("");

        String rolStr = (String) tableModel.getValueAt(row, 2);
        cmbRol.setSelectedItem(Rol.valueOf(rolStr));
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void agregar() {
        txtEmail.setEditable(true);

        String error = controller.agregar(
            usuarioActual,
            txtNombre.getText(),
            txtEmail.getText(),
            new String(txtClave.getPassword()),
            (Rol) cmbRol.getSelectedItem()
        );

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "User added successfully.");
            cargarTabla();
            limpiarCampos();
        }
    }

    private void actualizar() {
        String emailObjetivo;

        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
            emailObjetivo = txtEmail.getText().trim();
            if (emailObjetivo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please select a user from the table or enter an email.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            emailObjetivo = usuarioActual.getEmail();
        }

        String error = controller.actualizar(
            usuarioActual,
            emailObjetivo,
            txtNombre.getText().trim(),
            new String(txtClave.getPassword())
        );

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "User updated successfully.");
            cargarTabla();
            limpiarCampos();
        }
    }

    private void eliminar() {
        String email = txtEmail.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a user from the table.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate the user: " + email + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String error = controller.eliminar(usuarioActual, email);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User deactivated successfully.");
                cargarTabla();
                limpiarCampos();
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void cargarTabla() {
        tableModel.setRowCount(0);
        for (Usuario u : controller.listarTodos()) {
            tableModel.addRow(new Object[]{
                u.getNombre(),
                u.getEmail(),
                u.getRol().toString(),
                u.isActivo() ? "Active" : "Inactive"
            });
        }
    }

    private void limpiarCampos() {
        txtClave.setText("");

        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {
            txtNombre.setText("");
            txtEmail.setText("");
            cmbRol.setSelectedIndex(0);
        }
        // USER keeps their data loaded, only password is cleared
    }

    // ── Variables ─────────────────────────────────────────────────────────────
    private JTextField     txtNombre;
    private JTextField     txtEmail;
    private JPasswordField txtClave;
    private JComboBox<Rol> cmbRol;
    private JButton        btnAgregar;
    private JButton        btnActualizar;
    private JButton        btnEliminar;
    private JButton        btnLimpiar;
    private JTable         tabla;
}