package memoizeit.tuples.serialization;

import memoizeit.asm.util.ids.IndexGenerator;
import memoizeit.tuples.profiler.Profiler;

import org.objectweb.asm.Type;

public interface Types {
	
	public final Type PROFILER = Type.getType(Profiler.class);

	public final Type TRAVERSER = Type.getType(ITraverser.class);
	public final Type SERILIAZER_HELPER = Type.getType(AbstractSerializerHelper.class);
	public final Type SERILIAZER_JSON_HELPER = Type.getType(JSONSerializerHelper.class);
	public final Type SERILIAZER_HASHER_HELPER = Type.getType(HasherSerializerHelper.class);
	
	public final Type INDEX_GENERATOR = Type.getType(IndexGenerator.class);		
	public final Type OBJECT = Type.getType(Object.class);
	
	public final Type OBJECT_TAG = Type.getType(ObjectTag.class);
	
}
