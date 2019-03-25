var hexSize = 20;
var hexHeight = hexSize * 2;
var state = {
    hexSize: hexSize,
    canvasSize: {
	canvasWidth: 800,
	canvasHeight: 600
    },
    hexOrigin: {
	x: 400,
	y: 300,
    },
    hexParameters: {
	hexHeight: hexHeight,
	hexWidth: Math.sqrt(3)/2 * hexHeight,
	vertDist: hexHeight * 3/4,
	horizDist: Math.sqrt(3)/2 * hexHeight
    }
}

function hexToPixel(h) {
    var hexOrigin = state.hexOrigin;
    var x = state.hexSize * Math.sqrt(3) * (h.q + h.r/2) + hexOrigin.x;
    var y = state.hexSize * 3/2 * h.r + hexOrigin.y;
    return Point(x, y);
}

function drawLine(canvasID, start, end) {
    var ctx = canvasID.getContext("2d");
    ctx.beginPath();
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(end.x, end.y);
    ctx.stroke();
    ctx.closePath();
}

function drawHex(canvasID, center) {
    for (var i=0; i<=5; i++) {
	var start = getHexCornerCoord(center, i);
	var end = getHexCornerCoord(center, i+1);
	drawLine(canvasID, {x: start.x, y: start.y}, {x: end.x, y: end.y});
    }
}

function Hex(q, r) {
    return {
	q: q,
	r: r
    }
}

function drawHexCoordinates(canvasID, center, h) {
    var ctx = canvasID.getContext("2d");
    ctx.fillText(h.r, center.x+7, center.y);
    ctx.fillText(h.q, center.x-7, center.y);
}

function drawHexes() {
    var canvasWidth = state.canvasSize.canvasWidth;
    var canvasHeight = state.canvasSize.canvasHeight;
    var hexWidth = state.hexParameters.hexWidth;
    var hexHeight = state.hexParameters.hexWidth;
    var vertDist = state.hexParameters.hexWidth;
    var horizDist = state.hexParameters.hexWidth;
    var hexOrigin = state.hexOrigin;
    
    var qLeftSide = Math.round(hexOrigin.x/horizDist);
    var qRightSide = Math.round(canvasWidth-hexOrigin.x)/horizDist;
    var rTopSide = Math.round(hexOrigin.y/(vertDist));
    var rBottomSide = Math.round((canvasHeight-hexOrigin.y)/vertDist);
    console.log(qLeftSide, qRightSide, rTopSide, rBottomSide);
    var p=0;
    for (let r = 0; r <= rBottomSide; r++) {
	if (r%2 == 0 && r !== 0) p++;
	for (let q = -qLeftSide; q <= qRightSide; q++) {
	    var pix = hexToPixel(Hex(q-p, r));
	    var x = pix.x;
	    var y = pix.y;
	    if (x > hexWidth/2 &&
	        x < canvasWidth - hexWidth/2 &&
	        y > hexHeight/2 &&
	        y < canvasHeight - hexHeight/2) {
		drawHex(canvasHex, Point(x, y));
		drawHexCoordinates(canvasHex, Point(x, y), Hex(q-p, r));
	    }
	}
    }
    var n=0;
    for (let r = -1; r >= -rTopSide; r--) {
	if (r%2 !== 0) n++;
	for (let q = -qLeftSide; q <= qRightSide; q++) {
	    var pix = hexToPixel(Hex(q+n, r));
	    var x = pix.x;
	    var y = pix.y;
	    if (x > hexWidth/2 &&
	        x < canvasWidth - hexWidth/2 &&
	        y > hexHeight/2 &&
	        y < canvasHeight - hexHeight/2) {
		drawHex(canvasHex, Point(x, y));
		drawHexCoordinates(canvasHex, Point(x, y), Hex(q+n, r));
	    }
	}
    }
}

/*
function drawHexes() {
    var canvasWidth = state.canvasSize.canvasWidth;
    var canvasHeight = state.canvasSize.canvasHeight;
    var hexWidth = state.hexParameters.hexWidth;
    var hexHeight = state.hexParameters.hexWidth;
    var vertDist = state.hexParameters.hexWidth;
    var horizDist = state.hexParameters.hexWidth;
    var hexOrigin = state.hexOrigin;
    
    var qLeftSide = Math.round(hexOrigin.x/hexWidth)*4;
    var qRightSide = Math.round(canvasWidth-hexOrigin.x)/hexWidth*4;
    var rTopSide = Math.round(hexOrigin.y/(hexHeight/2));
    var rBottomSide = Math.round((canvasHeight-hexOrigin.y)/(hexHeight/2));
    
    var cv = document.getElementById('canvasHex');
    for (var r=-rTopSide; r <= rBottomSide; r++) {
	for (var q=-qLeftSide; q <= qRightSide; q++) {
	    // console.log(r, q);
	    let center = hexToPixel(Hex(q, r));
	    // console.log(center);
	    if (center.x > hexWidth/2 &&
	        center.x < canvasWidth - hexWidth/2 &&
	        center.y > hexHeight/2 &&
	        center.y < canvasHeight - hexHeight/2) {
		drawHex(cv, center);
		drawHexCoordinates(cv, center, Hex(q,r));
	    }

	}
    }
}
*/
function getHexCornerCoord(center, i) {
    var angle_deg = 60 * i + 30;
    var angle_rad = Math.PI / 180 * angle_deg;
    var x = center.x + state.hexSize * Math.cos(angle_rad);
    var y = center.y + state.hexSize * Math.sin(angle_rad);
    return Point(x, y);
}

function Point(x, y) {
    return {x: x, y: y};
}

document.addEventListener("DOMContentLoaded", function(event) {
    // var cv = document.getElementById('canvasHex');
    // drawHex(cv, { x: 50, y: 50 });
    drawHexes();
});
