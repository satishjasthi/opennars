/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nars.audio.granular.depr.ui;

import nars.audio.granular.depr.Envelope;
import nars.audio.granular.depr.Granulator;
import nars.audio.granular.depr.io.TextToWave;
import nars.audio.granular.depr.io.WaveToText;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author soote
 */
public class GranularSynthesizer extends javax.swing.JFrame {

    Granulator wave;
    int sampleCount;
    /**
     * Creates new form GranularSynthesizer
     */
    public GranularSynthesizer() {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GranularSynthesizer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GranularSynthesizer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GranularSynthesizer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GranularSynthesizer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        initComponents();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File",
                "txt");
        fileChooser.setFileFilter(filter);
        // Listen for changes in the text
        grainSizeTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (isNumeric(grainSizeTxt.getText())) {
                    if (Integer.parseInt(grainSizeTxt.getText()) != 0) {
                        maxGrainsLbl.setText("" + sampleCount / Integer
                                .parseInt(grainSizeTxt.getText()));
                    }
                }

            }
        });
    }
    //taken from https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-a-numeric-type-in-java
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new JFileChooser();
        modeChoice = new javax.swing.ButtonGroup();
        envelopeType = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        grainDensityTxt = new javax.swing.JTextField();
        defaultValues = new javax.swing.JButton();
        userSampleInput = new javax.swing.JTextField();
        openFile = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        cloudDurationTxt = new javax.swing.JTextField();
        grainDistanceTxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pitchSlider = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        createGrains = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        samplesLoadedLbl = new javax.swing.JLabel();
        numberOfGrainsTxt = new javax.swing.JTextField();
        sizeDeviationSlider = new javax.swing.JSlider();
        granulate = new javax.swing.JButton();
        maxGrainsLbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pitchDeviationSlider = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        grainSizeTxt = new javax.swing.JTextField();
        syncChoice = new javax.swing.JRadioButton();
        asyncChoice = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        noneChoice = new javax.swing.JRadioButton();
        asrChoice = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setForeground(java.awt.Color.black);

        jPanel1.setForeground(java.awt.Color.cyan);

        grainDensityTxt.setText("1");
        grainDensityTxt.setEnabled(false);

        defaultValues.setText("Default Values");
        defaultValues.setEnabled(false);
        defaultValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultValuesActionPerformed(evt);
            }
        });

        openFile.setText("...");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setText("Max:");

        cloudDurationTxt.setText("44400");
        cloudDurationTxt.setEnabled(false);

        grainDistanceTxt.setText("0");
        grainDistanceTxt.setEnabled(false);

        jLabel6.setForeground(new java.awt.Color(0, 255, 0));
        jLabel6.setText("Cloud Duration:");

        pitchSlider.setEnabled(false);

        jLabel5.setForeground(new java.awt.Color(0, 255, 0));
        jLabel5.setText("Grain Distance:");

        createGrains.setText("(Re)Create Grains");
        createGrains.setEnabled(false);
        createGrains.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createGrainsActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(0, 255, 0));
        jLabel7.setText("Grain Density:");


        numberOfGrainsTxt.setText("10");
        numberOfGrainsTxt.setEnabled(false);

        sizeDeviationSlider.setEnabled(false);

        granulate.setText("Granulate");
        granulate.setEnabled(false);
        granulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                granulateActionPerformed(evt);
            }
        });

        maxGrainsLbl.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        maxGrainsLbl.setForeground(new java.awt.Color(255, 0, 0));
        maxGrainsLbl.setText("   ");

        jLabel3.setForeground(new java.awt.Color(0, 255, 0));
        jLabel3.setText("Grain Size:");

        jLabel2.setForeground(new java.awt.Color(0, 255, 0));
        jLabel2.setText("Pitch Deviation:");

        jLabel12.setForeground(new java.awt.Color(0, 255, 0));
        jLabel12.setText("# of Grains to Create");

        jLabel1.setForeground(new java.awt.Color(0, 255, 0));
        jLabel1.setText("Size Deviation:");

        jLabel11.setForeground(new java.awt.Color(0, 255, 0));
        jLabel11.setText("Samples Loaded:");

        pitchDeviationSlider.setEnabled(false);

        jLabel4.setForeground(new java.awt.Color(0, 255, 0));
        jLabel4.setText("Pitch:");

        grainSizeTxt.setText("44400");
        grainSizeTxt.setEnabled(false);
        grainSizeTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grainSizeTxtActionPerformed(evt);
            }
        });
        grainSizeTxt.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                grainSizeTxtPropertyChange(evt);
            }
        });

        modeChoice.add(syncChoice);
        syncChoice.setSelected(true);
        syncChoice.setText("Synchronous");
        syncChoice.setEnabled(false);

        modeChoice.add(asyncChoice);
        asyncChoice.setText("Asynchronous");
        asyncChoice.setEnabled(false);

        jLabel9.setForeground(new java.awt.Color(0, 255, 0));
        jLabel9.setText("Envolope Type:");

        envelopeType.add(noneChoice);
        noneChoice.setText("None");
        noneChoice.setEnabled(false);

        envelopeType.add(asrChoice);
        asrChoice.setSelected(true);
        asrChoice.setText("ASR");
        asrChoice.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(syncChoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(asyncChoice)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(defaultValues))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(userSampleInput, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(openFile, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addComponent(jLabel13)
                                        .addGap(13, 13, 13)
                                        .addComponent(maxGrainsLbl))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel4)
                                        .addGap(130, 130, 130)
                                        .addComponent(pitchSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel2)
                                        .addGap(59, 59, 59)
                                        .addComponent(pitchDeviationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel5)
                                        .addGap(61, 61, 61)
                                        .addComponent(grainDistanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel6)
                                        .addGap(59, 59, 59)
                                        .addComponent(cloudDurationTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel12)
                                        .addGap(31, 31, 31)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(samplesLoadedLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(numberOfGrainsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(64, 64, 64)
                                                .addComponent(sizeDeviationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel9)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel3)
                                        .addGap(103, 103, 103)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(noneChoice)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(asrChoice))
                                            .addComponent(grainSizeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(createGrains)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(granulate))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(jLabel7)
                                            .addGap(69, 69, 69)
                                            .addComponent(grainDensityTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(userSampleInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(openFile))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(samplesLoadedLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(defaultValues))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(numberOfGrainsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(maxGrainsLbl))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(grainSizeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(noneChoice)
                        .addComponent(asrChoice))
                    .addComponent(jLabel9))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(sizeDeviationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(pitchSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(pitchDeviationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(grainDistanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(cloudDurationTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(grainDensityTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(syncChoice)
                    .addComponent(asyncChoice))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createGrains)
                    .addComponent(granulate))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        fileChooser
                .setCurrentDirectory(new File(
                                System.getProperty("user.dir") + "/src/music"));
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            userSampleInput.setText(file.getAbsolutePath());

            ArrayList<String> customSample = new ArrayList();
            try {
                customSample = TextToWave.readTxt(
                        System.getProperty("user.dir")
                        + "/src/music/" + file.getName());
            } catch (IOException ex) {
            }

            int[] headers = TextToWave.parseHeader(customSample);
            for (int i = 0; i < 5; i++)
                customSample.remove(0);

            String fn = "granned-" + file.getName();

            wave = new Granulator(
                    headers[0], //sampleCount
                    headers[1], //bitsPerSample
                    headers[2], //channels
                    headers[3], //sampleRate
                    headers[4], //frequency
                    customSample, //samples for custome clip
                    fn); //fileName
            samplesLoadedLbl.setText("" + headers[0]);
            sampleCount = headers[0];

            grainSizeTxt.setText(grainSizeTxt.getText());
            maxGrainsLbl.setText("" + sampleCount / Integer.parseInt(
                    grainSizeTxt.getText()));

            grainSizeTxt.setEnabled(true);
            numberOfGrainsTxt.setEnabled(true);
            sizeDeviationSlider.setEnabled(true);
            pitchSlider.setEnabled(true);
            pitchDeviationSlider.setEnabled(true);
            createGrains.setEnabled(true);
            defaultValues.setEnabled(true);
            asrChoice.setEnabled(true);
            noneChoice.setEnabled(true);
            defaultValuesActionPerformed(evt);
        } else {
            System.out.println("NOTHING");

        }
    }//GEN-LAST:event_openFileActionPerformed

    private void createGrainsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createGrainsActionPerformed
        //number of grains to create
        int numberOfGrains = Integer.parseInt(numberOfGrainsTxt.getText());
        //Size of grain
        int grainSize = Integer.parseInt(grainSizeTxt.getText());
        //random size deviation
        double sizeDeviation = (double) sizeDeviationSlider.getValue() / 100;//[0:no deviation, 1: max deviation]

        //pitch of grain, 1 is normal
        double grainPitch = (double) pitchSlider.getValue() / 50;
        //random pitch deviation
        double pitchDeviation = (double) pitchDeviationSlider.getValue() / 100;//[0:no deviation, 1: max deviation]

        Envelope e;
        if (noneChoice.isSelected()) {
            e = Envelope.NONE;
        } else {
            e = Envelope.TRAPEXIUM;
        }
        maxGrainsLbl.setText("" + wave.getSampleCount() / grainSize);

        wave.createGrains(numberOfGrains, grainSize, sizeDeviation, grainPitch,
                pitchDeviation, e);
        grainDistanceTxt.setEnabled(true);
        cloudDurationTxt.setEnabled(true);
        grainDensityTxt.setEnabled(true);
        granulate.setEnabled(true);
        syncChoice.setEnabled(true);
        asyncChoice.setEnabled(true);
    }//GEN-LAST:event_createGrainsActionPerformed

    private void granulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_granulateActionPerformed
        synthesize();
    }//GEN-LAST:event_granulateActionPerformed

    private void grainSizeTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grainSizeTxtActionPerformed

    }//GEN-LAST:event_grainSizeTxtActionPerformed

    private void grainSizeTxtPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_grainSizeTxtPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_grainSizeTxtPropertyChange

    private void defaultValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultValuesActionPerformed
        grainDistanceTxt.setText("" + 0);
        cloudDurationTxt.setText("" + 0);
        grainDensityTxt.setText("" + 1);
        sizeDeviationSlider.setValue(50);
        pitchDeviationSlider.setValue(50);
        pitchSlider.setValue(50);
        numberOfGrainsTxt.setText("" + sampleCount / Integer.parseInt(
                grainSizeTxt.getText()));
    }//GEN-LAST:event_defaultValuesActionPerformed

    private void synthesize() {
        String fn = "hotel";
        //Size of grain
        int grainSize = Integer.parseInt(grainSizeTxt.getText());
        //random size deviation
        double sizeDeviation = sizeDeviationSlider.getValue() / 100;//[0:no deviation, 1: max deviation]

        //pitch of grain, 1 is normal
        double grainPitch = (double) pitchSlider.getValue() / 50;
        //random pitch deviation
        double pitchDeviation = pitchDeviationSlider.getValue() / 100;//[0:no deviation, 1: max deviation]

        //distance between grains in the grain cloud
        int grainDistance = Integer.parseInt(grainDistanceTxt.getText());

        //size of the grain cloud, must be at least 1 grain in length
        int cloudDuration = Integer.parseInt(cloudDurationTxt.getText()) + (grainSize + grainDistance);
        //number of grains to randomely distribute into grain cloud
        int grainDensity = Integer.parseInt(grainDensityTxt.getText());

        boolean SYNCMODE = syncChoice.isSelected();

        wave.synthesize(grainSize, sizeDeviation,Integer.parseInt(numberOfGrainsTxt.getText()), grainPitch, pitchDeviation,
                grainDistance, grainDensity, cloudDuration, SYNCMODE);
        WaveToText.writeTxt(wave);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton asrChoice;
    private javax.swing.JRadioButton asyncChoice;
    private javax.swing.JTextField cloudDurationTxt;
    private javax.swing.JButton createGrains;
    private javax.swing.JButton defaultValues;
    private javax.swing.ButtonGroup envelopeType;
    private JFileChooser fileChooser;
    private javax.swing.JTextField grainDensityTxt;
    private javax.swing.JTextField grainDistanceTxt;
    private javax.swing.JTextField grainSizeTxt;
    private javax.swing.JButton granulate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel maxGrainsLbl;
    private javax.swing.ButtonGroup modeChoice;
    private javax.swing.JRadioButton noneChoice;
    private javax.swing.JTextField numberOfGrainsTxt;
    private javax.swing.JButton openFile;
    private javax.swing.JSlider pitchDeviationSlider;
    private javax.swing.JSlider pitchSlider;
    private javax.swing.JLabel samplesLoadedLbl;
    private javax.swing.JSlider sizeDeviationSlider;
    private javax.swing.JRadioButton syncChoice;
    private javax.swing.JTextField userSampleInput;
    // End of variables declaration//GEN-END:variables

}
