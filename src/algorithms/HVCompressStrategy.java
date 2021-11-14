package algorithms;

import model.RegionGraph;

public class HVCompressStrategy extends CompressStrategy {

	public HVCompressStrategy(RegionGraph graph) {
		super(graph);
	}

	@Override
	protected void compress(RegionGraph graph) {
		super.name = "HVCompressStrategy";

	}

}

