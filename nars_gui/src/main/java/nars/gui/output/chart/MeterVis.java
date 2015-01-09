package nars.gui.output.chart;


import automenta.vivisect.Video;
import automenta.vivisect.swing.PCanvas;
import automenta.vivisect.timeline.AxisPlot;
import automenta.vivisect.timeline.AxisPlot.MultiChart;
import automenta.vivisect.timeline.LineChart;
import automenta.vivisect.timeline.TimelineVis;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import nars.core.EventEmitter.EventObserver;
import nars.core.Events.FrameEnd;
import nars.core.NAR;
import nars.io.meter.Metrics;
import nars.io.meter.SignalData;
import nars.io.meter.Signal;
import nars.io.meter.TemporalMetrics;

public class MeterVis extends TimelineVis {
    private final NAR nar;

    
    @Deprecated public class DataChart {
        
        public final SignalData data;
        public AxisPlot chart;

        public DataChart(String id) {
            this.data = meters.newSignalData(id);
            this.chart = displayedCharts.get(id);
            
            if (chart!=null)
                chart.setOverlayEnable(true);
        }        

        private DataChart(Signal s) {
            this(s.id);
        }
    }
    
    final Map<String, AxisPlot> displayedCharts = new HashMap();
    final Metrics meters;
    final Map<String, DataChart> charts;
    
    
    public static final Font monofontLarge = Video.monofont.deriveFont(Font.PLAIN, 18f);
    public static final Font monofontSmall = Video.monofont.deriveFont(Font.PLAIN, 13f);
    int nextChartTime = 0;
    

    
    public MeterVisPanel newPanel() {
        return new MeterVisPanel();
    }
    
    public MeterVis(NAR nar, TemporalMetrics<Object> meters) {
        this(nar, meters, null);
    }
    
    /**
     *
     * @param title
     * @param historySize
     * @param chartType use Chart.BAR, Chart.LINE, Chart.PIE, Chart.AREA,
     * Chart.BAR_CENTERED
     */
    public MeterVis(NAR nar, TemporalMetrics<Object> meters, String[] enabled) {
        super();

        this.nar = nar;        
        this.meters = meters;

        charts = new TreeMap();
        
        if (enabled == null) {
            List<AxisPlot> c = new ArrayList();
            List<SignalData> signals = meters.getSignalDatas();
            for (SignalData s : signals) {
                charts.put(s.getID(), new DataChart(s.signal));                
                c.add(new LineChart(s).height(10));
            }            
            setCharts(c);
        }
        else {
            for (String f : enabled) {
                charts.put(f, new DataChart(f));            
            }
        }
        
        
    }
    


    public DataChart getDisplayedChart(String id) {
        if (id.contains("#")) {
            String baseName = id.split("#")[0];
            return getDisplayedChart(baseName);
        }
        DataChart c = charts.get(id);
        if (c!=null) {
            if (c instanceof MultiChart)
                ((MultiChart)c).getData().add(c.data);
            else
                throw new RuntimeException(c + " does not support multiple datas");
        }
        else {
            /*c = meters.newDefaultChart(id, data);
            
            if (c == null)
                c = new LineChart(data);
            
            displayedCharts.put(id, c);
            addChart(c);            */
        }
        return c;
    }    
    
    
    public class MeterVisPanel extends PCanvas implements EventObserver {
        
        public MeterVisPanel() {
            super(MeterVis.this);
            
            noLoop();
            
            //TODO disable event when window hiden
            nar.on(FrameEnd.class, this);
            
            final MouseAdapter m = newMouseDragPanScale(this);
            addMouseMotionListener(m);
            addMouseListener(m);
        }
        
        @Override
        public void event(Class event, Object[] args) {
            if (event == FrameEnd.class) {
                predraw();
                redraw();
            }
        }
        
    }
}
