package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;

public abstract class AbstractText implements Text {

	@Override
	public Text plus(Text v) {
		return new PlusText(this, v);
	}

	@Override
	public Text remove(Text v) {
		return new RemoveText(this, v);
	}

	@Override
	public Text replace(Text v1, Text v2) {
		return new ReplaceText(this, v1, v2);
	}

	@Override
	public Text upper() {
		return new UpperText(this);
	}

	@Override
	public Text lower() {
		return new LowerText(this);
	}

	@Override
	public Text trim() {
		return new TrimText(this);
	}
	
	@Override
	public Text substring(int beginIndex, int endIndex) {
		return new SubstringText(this, beginIndex, endIndex);
	}

	@Override
	public Num length() {
		return new LengthText(this);
	}

}
