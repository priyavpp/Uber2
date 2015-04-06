%Basic Configuration
NumVar = 3;
Element_x = 2;
Element_y = 3;
x_h = 1/NumVar/Element_x;
y_h = 1/Element_y;
Col = {'.r','.b','.g'};

%File Reading
A = load('./test.dat');
[n,m] = size(A);
meshsize = sqrt((m-1)/(NumVar+2));

%axis tight;
%set(gca, 'nextPlot', 'replace');

for i = 1 : n
    %Time i:
    %Mesh Plot
    plot(0,0);
    set(gca, 'Xlim', [0, meshsize], 'Ylim', [0, meshsize]);
    set(gcf, 'Color', 'white');
    axis off;
    hold on;
    for k = 0 : meshsize
        line([0, meshsize], [k,k], 'Color', 'k');
        line([k, k], [0, meshsize], 'Color', 'k');
    end

    for grid = 1 : meshsize*meshsize
        gridCoord = [A(i, 2 + (grid-1)*(NumVar+2)), A(i, 3 + (grid-1)*(NumVar+2))];
        Var = A(i, (4+(grid-1)*(NumVar+2)):(3+NumVar+(grid-1)*(NumVar+2)));
        for ii = 1 : NumVar
            DrawNPoints(gridCoord(1) + (ii-1)/NumVar, gridCoord(2), x_h, y_h, Element_x, Element_y, Var(ii), Col{ii});
        end
    end
    title(['Time T =', num2str(i), ' sec']);
    hold off
    anim(i) = getframe(gcf);

    im = frame2im(anim(i));
    [I,map]=rgb2ind(im,65536);
    if i==1
        imwrite(I,map,'mygif.gif','gif','Loopcount',1,'DelayTime',1);
    else
        imwrite(I,map,'mygif.gif','gif','WriteMode','append','DelayTime',1);  
    end
end

%movie(anim, 1, 1)



