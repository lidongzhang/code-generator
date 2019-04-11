package app.gui;

import app.api.generator.Util;

import javax.swing.*;

public class ProcesserTask implements  Runnable {

    Processer processer;

    public ProcesserTask(Processer processer){
        this.processer = processer;
    }

    @Override
    public void run(){
        try {
            while (true) {
                Thread.sleep(1000);
                int index = Util.getProcessIndex();
                processer.progressBar.setValue(index);

                System.out.println("run...");
                if(index >= 100 ) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(null, "生成完毕！");
                            processer.dialog.dispose();
                        }
                    });
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
