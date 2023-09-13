renderZeroCompensation = function(chart) {
  // get data meta, we need the location info in _view property.
  for (var i = 0; i < chart.data.datasets.length; i++) {
    const meta = chart.getDatasetMeta(i);
    // also you need get datasets to find which item is 0.
    const dataSet = chart.config.data.datasets[0].data;
    meta.data.forEach(function(d, index) {
      // for the item which value is 0, render a line.
      if (dataSet[index] === 0) {
        // this.renderZeroCompensation(chart, d)
        // get position info from _view
        const view = d._view;
        const context = chart.chart.ctx;

        // the view.x is the central point of the bar, so we need minus half width of the bar.
        const startX = view.x - view.width / 2;
        // common canvas API, Check it out on MDN
        context.beginPath();
        // set line color, you can do more custom settings here.
        context.strokeStyle = '#aaaaaa';
        context.moveTo(startX, view.y);
        // draw the line!
        context.lineTo(startX + view.width, view.y);
        // bam！ you will see the lines.
        context.stroke();
      }
    });
  }
};

const zeroCompensation = {
  afterDatasetsDraw: function(chart, easing) {
    renderZeroCompensation(chart);
  },

  afterDatasetsUpdate: function(chart, easing) {
    renderZeroCompensation(chart);
  }
};
