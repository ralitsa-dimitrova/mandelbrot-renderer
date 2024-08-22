public class MbrotContext {
    public final int maxIterations;
    public final double aStart;
    public final double aEnd;
    public final double bStart;
    public final double bEnd;
    public final double pointOffsetA;
    public final double pointOffsetB;
    //
    public final int width;
    public final int height;
    public final String outputFileNamePrefix;
    public final int threadCount;
    public final int granularity;
    public final Constants.LoadBalancing loadBalancing;
    public final Constants.MatrixDivision matrixDivision;

    public final boolean quietMode;
    public final boolean compareThreadsMode;
    public final boolean helpMode;

    private MbrotContext(MbrotContextBuilder builder) {
        this.maxIterations = builder.maxIterations;
        this.aStart = builder.aStart;
        this.aEnd = builder.aEnd;
        this.bStart = builder.bStart;
        this.bEnd = builder.bEnd;
        this.width = builder.width;
        this.height = builder.height;
        this.outputFileNamePrefix = builder.outputFileNamePrefix;
        this.threadCount = builder.threadCount;
        this.granularity = builder.granularity;
        this.loadBalancing = builder.loadBalancing;
        this.matrixDivision = builder.matrixDivision;
        this.quietMode = builder.quietMode;
        this.compareThreadsMode = builder.compareThreadsMode;
        this.helpMode = builder.helpMode;

        this.pointOffsetA = Math.abs(this.aEnd - this.aStart) / this.width;
        this.pointOffsetB = Math.abs(this.bEnd - this.bStart) / this.height;
    }

    public static class MbrotContextBuilder {
        private int maxIterations = 1000;
        private double aStart = -2.0;
        private double aEnd = 2.0;
        private double bStart = -2.0;
        private double bEnd = 2.0;

        private int width = 640;
        private int height = 480;
        private String outputFileNamePrefix = "zad21";
        private int threadCount = 1;
        private int granularity = 1;
        private Constants.LoadBalancing loadBalancing = Constants.LoadBalancing.STATIC;
        private Constants.MatrixDivision matrixDivision = Constants.MatrixDivision.ROWS;

        private boolean quietMode = false;
        private boolean compareThreadsMode = false;
        private boolean helpMode = false;


        public MbrotContextBuilder maxIterations(int maxIterations){
            this.maxIterations = maxIterations;
            return this;
        }

        public MbrotContextBuilder aStart(double aStart){
            this.aStart = aStart;
            return this;
        }

        public MbrotContextBuilder aEnd(double aEnd){
            this.aEnd = aEnd;
            return this;
        }

        public MbrotContextBuilder bStart(double bStart){
            this.bStart = bStart;
            return this;
        }

        public MbrotContextBuilder bEnd(double bEnd){
            this.bEnd = bEnd;
            return this;
        }

        public MbrotContextBuilder width(int width){
            this.width = width;
            return this;
        }

        public MbrotContextBuilder height(int height){
            this.height = height;
            return this;
        }

        public MbrotContextBuilder outputFileNamePrefix(String outputFileNamePrefix){
            this.outputFileNamePrefix = outputFileNamePrefix;
            return this;
        }

        public MbrotContextBuilder threadCount(int threadCount){
            this.threadCount = threadCount;
            return this;
        }

        public MbrotContextBuilder granularity(int granularity){
            this.granularity = granularity;
            return this;
        }

        public MbrotContextBuilder loadBalancing(Constants.LoadBalancing loadBalancing){
            this.loadBalancing = loadBalancing;
            return this;
        }

        public MbrotContextBuilder matrixDivision(Constants.MatrixDivision matrixDivision){
            this.matrixDivision = matrixDivision;
            return this;
        }

        public MbrotContextBuilder quietMode(boolean quietMode){
            this.quietMode = quietMode;
            return this;
        }

        public MbrotContextBuilder compareThreadsMode(boolean compareThreadsMode){
            this.compareThreadsMode = compareThreadsMode;
            return this;
        }

        public MbrotContextBuilder helpMode(boolean helpMode){
            this.helpMode = helpMode;
            return this;
        }


        public MbrotContext build(){
            return new MbrotContext(this);
        }
    }

    public String printContext() {
        return String.join("\n", "MbrotContext",
                "maxIterations=" + maxIterations,
                "aStart=" + String.valueOf(aStart),
                "aEnd=" + String.valueOf(aEnd),
                "bStart=" + String.valueOf(bStart),
                "bEnd=" + String.valueOf(bEnd),
                "width=" + String.valueOf(width),
                "height=" + String.valueOf(height),
                "nThreads=" + String.valueOf(threadCount),
                "granularity=" + String.valueOf(granularity),
                "loadBalancing=" + String.valueOf(loadBalancing),
                "matrixDivision=" + String.valueOf(matrixDivision));
    }
}
