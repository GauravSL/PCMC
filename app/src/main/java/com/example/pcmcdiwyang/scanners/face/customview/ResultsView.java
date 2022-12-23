package com.example.pcmcdiwyang.scanners.face.customview;


import com.example.pcmcdiwyang.scanners.face.tflite.SimilarityClassifier;

import java.util.List;

public interface ResultsView {
  public void setResults(final List<SimilarityClassifier.Recognition> results);
}
