function DrawNPoints(Origin_x, Origin_y, x_h, y_h, num_x, num_y, N, col)
%
% Draw N points in the square defined by [Origin_x, Origin_y] and [Origin_x + num_x*x_h, Origin_y + num_y*y_h]. 

if(num_x*num_y < N)
      disp 'Wrong Input';
      return 
end

colNum = ceil(N/num_y);
fullColNum = floor(N/num_y);
res = N - num_y * fullColNum;

for ss = 1 : fullColNum
    xx = (Origin_x + (ss-.5)*x_h)*ones(1, num_y);
    yy = Origin_y + y_h*[1:num_y] - .5*y_h;
    scatter(xx, yy, 1000, col);
end

if fullColNum == colNum
    return 
else
    xx = (Origin_x + (colNum-.5)*x_h)*ones(1, res);
    yy = Origin_y + y_h*[1:res] - .5*y_h;
    scatter(xx, yy, 1000, col);
end





