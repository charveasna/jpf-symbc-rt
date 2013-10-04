package gov.nasa.jpf.symbc.realtime.scd_micro;

public class Main {
	public static void main(String[] argv)
	{
		Main m = new Main();
		for(int i = 0; i < 1; i++) {
			m.run();
		}
		System.out.println("Benchmark finished");
	}
	private FrameBuffer frameBuffer;
	private gov.nasa.jpf.symbc.realtime.scd_micro.NoiseGenerator noiseGenerator;
	private gov.nasa.jpf.symbc.realtime.scd_micro.TransientDetectorScopeEntry cd;

	public Main() {
		frameBuffer = new FrameBuffer();
		noiseGenerator = new NoiseGenerator();
		cd = new TransientDetectorScopeEntry(new StateTable(), Constants.GOOD_VOXEL_SIZE);		
	}
	public void init() {
		
	}
	public void run() {
		genFrame();
		RawFrame f = frameBuffer.getFrame();
		noiseGenerator.generateNoiseIfEnabled();

		cd.setFrame(f);
		cd.run();
	}
	byte callsigns_[] = new byte[50];
	int lengths_[] = new int[10];
	float positions_[] = new float[30];
	private void genFrame() {		
		frameBuffer.putFrame(positions_, lengths_, callsigns_);
	}	
}
