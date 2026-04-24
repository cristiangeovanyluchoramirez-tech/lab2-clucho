package com.mycompany.lab2clucho;

import com.mycompany.lab2clucho.controller.UserControlle;
import com.mycompany.lab2clucho.controller.UserControlle.Usuario;

public class MenuPrincipalView extends javax.swing.JFrame {

    private final Login loginRef;
    private final UserControlle controller;
    private final Usuario usuarioActual;

    public MenuPrincipalView(Login login, UserControlle controller, Usuario usuario) {
        this.loginRef      = login;
        this.controller    = controller;
        this.usuarioActual = usuario;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Main Menu - " + usuario.getNombre() 
         + " [" + usuario.getRol() + "]");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jMenuBar1       = new javax.swing.JMenuBar();
        jMenuGestion    = new javax.swing.JMenu();
        jMenuItemMant   = new javax.swing.JMenuItem();
        jMenuSesion     = new javax.swing.JMenu();
        jMenuItemCerrar = new javax.swing.JMenuItem();
        jLabel1         = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        // ── Menú Gestión ──────────────────────────────────────────
        jMenuGestion.setText("Management");

        jMenuItemMant.setText("User Maintenance");
        jMenuItemMant.addActionListener(e -> {
            MantenimientoUsuarios mu = 
                new MantenimientoUsuarios(controller, usuarioActual);
            mu.setVisible(true);
        });
        jMenuGestion.add(jMenuItemMant);

        // ── Menú Cerrar Sesión ─────────────────────────────────────
        jMenuSesion.setText("Log Out");
        jMenuItemCerrar.setText("Exit");
        jMenuItemCerrar.addActionListener(e -> cerrarSesion());
        jMenuSesion.add(jMenuItemCerrar);

        jMenuBar1.add(jMenuGestion);
        jMenuBar1.add(jMenuSesion);
        setJMenuBar(jMenuBar1);

        // ── Label central ──────────────────────────────────────────
        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome, " + usuarioActual.getNombre());

        javax.swing.GroupLayout layout = 
            new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 
                          400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 
                          300, Short.MAX_VALUE)
        );

        pack();
    }

    private void cerrarSesion() {
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?", "Confirm",
            javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            this.dispose();
            loginRef.mostrar();
        }
    }

    // Variables declaration
    private javax.swing.JMenuBar  jMenuBar1;
    private javax.swing.JMenu     jMenuGestion;
    private javax.swing.JMenu     jMenuSesion;
    private javax.swing.JMenuItem jMenuItemMant;
    private javax.swing.JMenuItem jMenuItemCerrar;
    private javax.swing.JLabel    jLabel1;
}