package com.indower.models;

public class AverageResult {
  
  private Float average = 0f;
  private Integer totalCount = 0;

  public AverageResult() {
  }

  public AverageResult(Float average, Integer totalCount) {
    this.average = average;
    this.totalCount = totalCount;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public Float getAverage() {
    return average;
  }

  public void setAverage(Float average) {
    this.average = average;
  }
}
