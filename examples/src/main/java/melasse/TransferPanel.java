package melasse;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.beans.PropertyChangeListener;

import java.text.MessageFormat;
import java.text.NumberFormat;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Image;

import java.awt.event.ActionEvent;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.BoundedRangeModel;
import javax.swing.AbstractAction;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.GroupLayout;

import melasse.util.AppUtils;

import melasse.swing.SpinnerNumberModel;

/**
 * Transfer panel.
 *
 * @author Cedric Chantepie
 */
public final class TransferPanel extends JPanel {
    // --- Shared ---

    /**
     * Pause icon
     */
    private static final ImageIcon TRANSFER_ICON;

    static {
        TRANSFER_ICON =
            new ImageIcon(TransferAction.class.
                          getResource("/melasse/" +
                                      "document-save.gif"));
        
    } // end of static

    // --- Properties ---

    /**
     * Owning application
     */
    private final Object application;

    /**
     * Application logger
     */
    private final Logger logger;

    /**
     * Estimated time label
     */
    private JLabel etaText = null;

    /**
     * Progress bar (view)
     */
    private JProgressBar progressBar = null;

    /**
     * Progress model
     */
    private BoundedRangeModel progressModel = null;

    /**
     * Data size model
     */
    private SpinnerNumberModel dataSizeModel = null;

    /**
     * Bandwidth model
     */
    private SpinnerNumberModel bandwidthModel = null;

    /**
     */
    private final PropertyChangeSupport pcs;

    /**
     * Data size
     */
    private Integer dataSize = null;

    /**
     * Bandwidth
     */
    private Integer bandwidth = null;

    /**
     * Progress description pattern
     */
    private String progressPattern = null;

    /**
     * Currently transfered
     */
    private Integer transfered = null;

    /**
     * Button
     */
    private JButton button = null;

    /**
     * Transfer action
     */
    private TransferAction transferAction = null;

    /**
     * Transfer simulation
     */
    private TransferSimulation simulation = null;

    // --- Constructors ---

    /**
     * Bulk constructor.
     *
     * @param application Owning application
     */
    protected TransferPanel(Object application) {
        super();

        if (application == null) {
            throw new IllegalArgumentException("Owner not specified");
        } // end of if

        // ---

        this.application = application;
        this.logger = AppUtils.getLogger(this.application);
        this.pcs = new PropertyChangeSupport<TransferPanel>(this);
        this.transfered = new Integer(0);
        this.dataSize = new Integer(0);
        this.simulation = new TransferSimulation();

        this.pcs.
            registerDependency("dataSize",
                               new String[] { 
                                   "estimatedTime",
                                   "remainingSize",
                                   "dataSizeKbyte"
                               });

        this.pcs.
            registerDependency("bandwidth",
                               new String[] { 
                                   "estimatedTime",
                                   "remainingSize"
                               });

        this.pcs.
            registerDependency("transfered",
                               new String[] { 
                                   "remainingSize"
                               });

        this.pcs.
            registerDependency("remainingSize",
                               new String[] { 
                                   "progressDescription"
                               });

        this.pcs.
            registerDependency("estimatedTime",
                               new String[] { 
                                   "progressDescription",
                               });

    } // end of <init>

    // --- Properties accessors ---

    /**
     */
    public void setTransfered(Integer transfered) {
        PropertyChangeSupport.PropertyEditSession s =
            this.pcs.propertyWillChange("transfered");

        this.transfered = transfered;

        s.propertyDidChange();
    } // end of setTransfered

    /**
     * Returns transfered size (kbyte).
     */
    public Integer getTransfered() {
        return this.transfered;
    } // end of getTransfered

    /**
     * Returns remaining size (kbyte).
     */
    public int getRemainingSize() {
        if (this.dataSize == null) {
            return 0;
        } // end of if

        if (this.transfered == null) {
            return this.dataSize.intValue() * 1024;
        } // end of if

        int r = (this.dataSize.intValue() * 1024) - 
            this.transfered.intValue();

        if (r < 0) {
            return 0;
        } // end of if

        return r;
    } // end of getRemainingSize

    /**
     */
    public void setDataSize(Integer dataSize) {
        PropertyChangeSupport.PropertyEditSession s =
            this.pcs.propertyWillChange("dataSize");

        this.dataSize = dataSize;

        s.propertyDidChange();
    } // end of setDataSize

    /**
     * Returns data size.
     */
    public Integer getDataSize() {
        return this.dataSize;
    } // end of getDataSize

    /**
     * Returns data size as kbyte.
     */
    public Integer getDataSizeKbyte() {
        if (this.dataSize == null) {
            return null;
        } // end of if

        return new Integer(this.dataSize.intValue() * 1024);
    } // end of getDataSizeKbyte

    /**
     */
    public void setBandwidth(Integer bandwidth) {
        PropertyChangeSupport.PropertyEditSession s =
            this.pcs.propertyWillChange("bandwidth");

        this.bandwidth = bandwidth;

        s.propertyDidChange();
    } // end of setBandwidth

    /**
     * Returns bandwidth.
     */
    public Integer getBandwidth() {
        return this.bandwidth;
    } // end of getBandwidth    

    /**
     * Returns estimated time.
     */
    public int getEstimatedTime() {
        if (this.dataSize == null ||
            this.bandwidth == null) {

            return -1;
        } // end of if

        // ---

        int b = this.bandwidth.intValue();

        if (b == 0) {
            return -1;
        } // end of if

        return (this.dataSize.intValue() * 1024) / b;
    } // end of getEstimatedTime

    /**
     * Returns (localized) progress description.
     */
    public String getProgressDescription() {
        String descr = MessageFormat.
            format(this.progressPattern,
                   new Object[] {
                       new Float(this.transfered.intValue() / 1024f),
                       this.dataSize,
                       new Float(getRemainingSize() / 1024f)
                   });

        return descr;
    } // end of getProgressDescription

    // ---

    /**
     * Adds property change |listener|.
     *
     * @param listener Listener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    } // end of addPropertyChangeListener

    /**
     * Removes property change |listener|.
     *
     * @param listener Listener to be removeed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    } // end of removePropertyChangeListener

    /**
     * Sets up UI.
     */
    protected void setUpUI() {
        logger.fine("Will set up transfer panel");

        Locale locale = (Locale) AppUtils.
            getProperty(this.application, "locale");

        logger.log(Level.FINER,
                   "locale = {0}", locale);

        String transferTitle = AppUtils.
            localizedString("melasse.TransferPanel",
                            locale,
                            "transfer.title");

        String dataSizeStr = AppUtils.
            localizedString("melasse.TransferPanel",
                            locale,
                            "dataSize.label");

        String bandwidthStr = AppUtils.
            localizedString("melasse.TransferPanel",
                            locale,
                            "bandwidth.label");

        String etaStr = AppUtils.
            localizedString("melasse.TransferPanel",
                            locale,
                            "estimatedTime.label");

        this.progressPattern = AppUtils.
            localizedString("melasse.TransferPanel",
                            locale,
                            "progress.pattern");

        // prepare labels
        JLabel dataSizeLabel = new JLabel("<html>" + dataSizeStr + "</html>");
        JLabel bandwidthLabel = new JLabel("<html>" + bandwidthStr + "</html>");
        JLabel etaLabel = new JLabel(etaStr);

        this.etaText = new JLabel("", JLabel.CENTER);

        // prepare progress view/model
        this.progressModel = new DefaultBoundedRangeModel();

        this.progressModel.setMinimum(0);
        this.progressModel.setValue(0);
        this.progressModel.setMaximum(0);

        this.progressBar = new JProgressBar(this.progressModel);

        this.progressBar.setStringPainted(true);

        // spinners view/model
        this.dataSizeModel = new SpinnerNumberModel();
        this.bandwidthModel = new SpinnerNumberModel();

        this.dataSizeModel.setMinimum(new Integer(0));
        this.dataSizeModel.setStepSize(new Integer(1));

        this.bandwidthModel.setMinimum(new Integer(0));
        this.bandwidthModel.setStepSize(new Integer(1));

        JSpinner dataSizeSpinner = new JSpinner(this.dataSizeModel);
        JSpinner bandwidthSpinner = new JSpinner(this.bandwidthModel);

        Dimension spinnerSize = dataSizeSpinner.getPreferredSize();

        spinnerSize.setSize(spinnerSize.getWidth() * 1.8f,
                            spinnerSize.getHeight());

        dataSizeSpinner.setPreferredSize(spinnerSize);
        bandwidthSpinner.setPreferredSize(spinnerSize);

        // explanation sub-panel
        ExplanationPanel explain = 
            new ExplanationPanel(this.application);

        explain.setTitleKey("transfer.title");
        explain.setBodyKey("transfer.body");
        explain.setCodeKey("transfer.code");

        explain.setUp();

        // Transfer sub-panel
        JPanel transferPanel = new JPanel();
        Dimension size = transferPanel.getPreferredSize();

        size.setSize(225, size.getHeight());

        transferPanel.setPreferredSize(size);

        // sets layout
        GroupLayout slayout = new GroupLayout(transferPanel);

        transferPanel.setLayout(slayout);

        slayout.setAutoCreateGaps(true);
        slayout.setAutoCreateContainerGaps(true);

        // lays out
        JSeparator vsep = new JSeparator(JSeparator.HORIZONTAL);

        // button
        this.transferAction = new TransferAction();

        this.button = new JButton(this.transferAction);

        this.button.setEnabled(false);

        GroupLayout.Group svgroup = 
            slayout.createSequentialGroup().
            addComponent(dataSizeLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(dataSizeSpinner,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(bandwidthLabel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(bandwidthSpinner,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(vsep,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addGroup(slayout.
                     createParallelGroup(GroupLayout.Alignment.LEADING).
                     addComponent(etaLabel,
                                  GroupLayout.PREFERRED_SIZE,
                                  GroupLayout.DEFAULT_SIZE,
                                  GroupLayout.PREFERRED_SIZE).
                     addComponent(this.etaText,
                                  GroupLayout.PREFERRED_SIZE,
                                  GroupLayout.DEFAULT_SIZE,
                                  GroupLayout.PREFERRED_SIZE)).
            addComponent(this.button,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(this.progressBar,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE);

        GroupLayout.Group shgroup = 
            slayout.createParallelGroup(GroupLayout.Alignment.LEADING).
            addComponent(dataSizeLabel,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(dataSizeSpinner,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(bandwidthLabel,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(bandwidthSpinner,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(vsep,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addGroup(slayout.createSequentialGroup().
                     addComponent(etaLabel,
                                  0,
                                  GroupLayout.DEFAULT_SIZE,
                                  Short.MAX_VALUE).
                     addComponent(this.etaText,
                                  GroupLayout.PREFERRED_SIZE,
                                  GroupLayout.DEFAULT_SIZE,
                                  GroupLayout.PREFERRED_SIZE)).
            addGroup(slayout.createSequentialGroup().
                     addGap(0, 0, Short.MAX_VALUE).
                     addComponent(this.button,
                                  GroupLayout.PREFERRED_SIZE,
                                  GroupLayout.DEFAULT_SIZE,
                                  GroupLayout.PREFERRED_SIZE)).
            addComponent(this.progressBar,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);
        
        slayout.setVerticalGroup(svgroup);
        slayout.setHorizontalGroup(shgroup);

        slayout.linkSize(SwingConstants.HORIZONTAL,
                         dataSizeLabel, bandwidthLabel, etaLabel);

        transferPanel.setBorder(BorderFactory.
                                createTitledBorder(transferTitle));

        // sets layout
        final GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // lays out
        GroupLayout.Group vgroup = 
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).
            addComponent(transferPanel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE).
            addComponent(explain,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);

        GroupLayout.Group hgroup = 
            layout.createSequentialGroup().
            addComponent(transferPanel,
                         GroupLayout.PREFERRED_SIZE,
                         GroupLayout.DEFAULT_SIZE,
                         GroupLayout.PREFERRED_SIZE).
            addComponent(explain,
                         0,
                         GroupLayout.DEFAULT_SIZE,
                         Short.MAX_VALUE);
        
        layout.setVerticalGroup(vgroup);
        layout.setHorizontalGroup(hgroup);
    } // end of setUpUI

    /**
     * Sets up bindings.
     */
    protected void setUpBindings() {
        Locale locale = (Locale) AppUtils.
            getProperty(this.application, "locale");

        Binder.bind("value", this.dataSizeModel,
                    "dataSize", this,
                    new BindingOptionMap());

        Binder.bind("value", this.bandwidthModel,
                    "bandwidth", this,
                    new BindingOptionMap());

        Binder.bind("estimatedTime", this,
                    "text", this.etaText,
                    new BindingOptionMap().
                    add(BindingKey.INPUT_TRANSFORMER,
                        NumberToStringTransformer.
                        getInstance(NumberFormat.
                                    getNumberInstance(locale))));

        Binder.bind("dataSize", this,
                    "maximum", this.progressModel,
                    BindingOptionMap.targetModeOptions);

        Binder.bind("progressDescription", this,
                    "string", this.progressBar,
                    BindingOptionMap.targetModeOptions);

        Binder.bind("dataSize", this,
                    "enabled[]", this.transferAction,
                    new BindingOptionMap().
                    add(BindingKey.INPUT_TRANSFORMER,
                        IntegerToBooleanTransformer.getInstance()));

        Binder.bind("bandwidth", this,
                    "enabled[]", this.transferAction,
                    new BindingOptionMap().
                    add(BindingKey.INPUT_TRANSFORMER,
                        IntegerToBooleanTransformer.getInstance()));
        
        Binder.bind("dataSizeKbyte", this,
                    "maximum", this.progressModel,
                    BindingOptionMap.targetModeOptions);

        Binder.bind("transfered", this,
                    "value", this.progressModel,
                    BindingOptionMap.targetModeOptions);

    } // end of setUpBindings

    // --- Inner classes ---

    /**
     * Pause action for filling panel.
     *
     * @author Cedric Chantepie ()
     */
    private class TransferAction extends AbstractAction {
        // --- Constructors ---

        /**
         * No-arg constructor.
         */
        protected TransferAction() {
            super("<transfer>", TRANSFER_ICON);

            Locale locale = (Locale) AppUtils.
                getProperty(application, "locale");
            String text = AppUtils.
                localizedString("melasse." +
                                "TransferPanel",
                                locale,
                                "transfer.button");
            String tooltip = AppUtils.
                localizedString("melasse." +
                                "TransferPanel",
                                locale,
                                "transfer.tooltip");

            int mnemo = AppUtils.
                localizedKey("melasse." +
                             "TransferPanel",
                             locale,
                             "transfer.mnemonic");

            this.putValue(AbstractAction.NAME, text);
            this.putValue(AbstractAction.SHORT_DESCRIPTION, tooltip);
            this.putValue(AbstractAction.MNEMONIC_KEY, new Integer(mnemo));

            logger.finer("Inited");
        } // end of <init>

        // ---

        /**
         * Close application, as not authenticated.
         */
        public void actionPerformed(ActionEvent e) {
            logger.fine("Will pause filling");

            if (!this.isEnabled()) {
                logger.fine("Cannot log in");

                return;
            } // end of if

            // ---

            setEnabled(false);
            setTransfered(0);

            Thread th = new Thread(simulation);

            th.start();
        } // end of actionPerformed
    } // end of class TransferAction

    /**
     * Transfer simulation.
     *
     * @author Cedric Chantepie ()
     */
    private class TransferSimulation implements Runnable {

        /**
         * {@inheritDoc}
         */
        public void run() {
            if (dataSize == null || bandwidth == null) {
                logger.log(Level.WARNING,
                           "Cannot transfer with current data size and bandwidth: {0}, {1}", new Object[] { dataSize, bandwidth });

                return;
            } // end of if

            // ---

            int max = dataSize.intValue() * 1024;
            int b = bandwidth.intValue();

            for (int i = 0; i < max; ) {
                setTransfered(i += bandwidth);

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                               "Fails to sleep", e);

                } // end of catch
            } // end of for

            transferAction.setEnabled(true);
        } // end of run
    } // end of class TransferSimulation
} // end of class TransferPanel
