# ArrowPopWindows

An simple android view library to show an arrow popwindows on particular view


# How to use

```java

ArrowPopWindows arrowPopWindows = new ArrowPopWindows(MainActivity.this, R.layout.layout, new ArrowPopWindows.OnViewCreateListener() {
    @Override
    public void onViewCreate(ViewGroup viewGroup) {

    }
});
arrowPopWindows.show(view);

```

the OnViewCreateListener is to do some thing when the view add to the popwindows
so you can add some listener or change the content of the view

### Choose the direction of the popwindows

you can add the param when call the method show:

>SHOW_LEFT
>SHOW_RIGHT
>SHOW_BLOW
>SHOW_TOP
>SHOW_VERTICAL_AUTO
>SHOW_HORIZON_AUTO

eg:

```java

arrowPopWindows.show(view, SHOW_TOP)

```

# Demo pic

<img src="https://raw.githubusercontent.com/Boelroy/ArrowPopupWindows/master/screen/screen.png" width="300">