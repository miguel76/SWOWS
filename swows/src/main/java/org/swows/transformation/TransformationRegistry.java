package org.swows.transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.vocabulary.RDF;
import org.swows.comp.DataflowFactory;
import org.swows.vocabulary.DF;
import org.topbraid.spin.vocabulary.SP;

public class TransformationRegistry {
	
	private Map<Node, TransformationFactory> map =
			new HashMap<Node, TransformationFactory>();
	
	public void register(Node configClass, TransformationFactory factory) {
		map.put(configClass, factory);
	}

	public void unregister(Node configClass, TransformationFactory factory) {
		if (map.get(configClass).equals(factory))
			map.remove(configClass);
	}

	private TransformationRegistry() {
		
		register(SP.Query.asNode(), QueryTransformation.getFactory());
		register(SP.Ask.asNode(), QueryTransformation.getFactory());
		register(SP.Select.asNode(), QueryTransformation.getFactory());
		register(SP.Construct.asNode(), QueryTransformation.getFactory());
		register(SP.Describe.asNode(), QueryTransformation.getFactory());
		
		register(SP.Update.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.InsertData.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.DeleteData.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.Modify.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.DeleteWhere.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.Load.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.Clear.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.Create.asNode(), UpdateRequestTransformation.getOperationFactory());
		register(SP.Drop.asNode(), UpdateRequestTransformation.getOperationFactory());
		
		register(DF.UpdateRequest.asNode(), UpdateRequestTransformation.getRequestFactory());

		register(DF.Dataflow.asNode(), DataflowFactory.get());

	}
	
	public Transformation transformationFromGraph(Graph configGraph, Node configRoot) {
		final Set<TransformationFactory> factories = new HashSet<TransformationFactory>();
		GraphUtil
				.listObjects(configGraph, configRoot, RDF.type.asNode())
				.filterKeep(new Predicate<Node>() {
					@Override
					public boolean test(Node configClass) {
						TransformationFactory factory = map.get(configClass);
						if (factory != null)
							factories.add(factory);
						return false;
					}
				}).hasNext();
		switch(factories.size()) {
		case 0:
			throw new RuntimeException(
					"No transformation factory found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		case 1:
			for (TransformationFactory factory : factories)
				return factory.transformationFromGraph(configGraph, configRoot);
		default:
			throw new RuntimeException(
					"Too much transformation factories found "
					+ "for node " + configRoot
					+ " in graph " + configGraph);
		}
	}
	
	private static TransformationRegistry singleton;
	
	public static TransformationRegistry get() {
		if (singleton == null)
			singleton = new TransformationRegistry();
		return singleton;
	}
	
}
