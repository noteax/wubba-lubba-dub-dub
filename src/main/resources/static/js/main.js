;(function () {

    'use strict';


    var isMobile = {
        Android: function () {
            return navigator.userAgent.match(/Android/i);
        },
        BlackBerry: function () {
            return navigator.userAgent.match(/BlackBerry/i);
        },
        iOS: function () {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i);
        },
        Opera: function () {
            return navigator.userAgent.match(/Opera Mini/i);
        },
        Windows: function () {
            return navigator.userAgent.match(/IEMobile/i);
        },
        any: function () {
            return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
        }
    };

    var fullHeight = function () {

        if (!isMobile.any()) {
            $('.js-fullheight').css('height', $(window).height());
            $(window).resize(function () {
                $('.js-fullheight').css('height', $(window).height());
            });
        }

    };

    var sliderMain = function () {

        $('#fh5co-hero .flexslider').flexslider({
            animation: "fade",
            slideshowSpeed: 5000,
            directionNav: true,
            start: function () {
                setTimeout(function () {
                    $('.slider-text').removeClass('animated fadeInUp');
                    $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
                }, 500);
            },
            before: function () {
                setTimeout(function () {
                    $('.slider-text').removeClass('animated fadeInUp');
                    $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
                }, 500);
            }

        });

        $('#fh5co-hero .flexslider .slides > li').css('height', $(window).height());
        $(window).resize(function () {
            $('#fh5co-hero .flexslider .slides > li').css('height', $(window).height());
        });

    };

    var centerBlock = function () {
        $('.fh5co-section-with-image .fh5co-box').css('margin-top', -($('.fh5co-section-with-image .fh5co-box').outerHeight() / 2));
        $(window).resize(function () {
            $('.fh5co-section-with-image .fh5co-box').css('margin-top', -($('.fh5co-section-with-image .fh5co-box').outerHeight() / 2));
        });
    };

    var responseHeight = function () {
        setTimeout(function () {
            $('.js-responsive > .v-align').css('height', $('.js-responsive > img').height());
        }, 1);

        $(window).resize(function () {
            setTimeout(function () {
                $('.js-responsive > .v-align').css('height', $('.js-responsive > img').height());
            }, 1);
        })
    };


    var mobileMenuOutsideClick = function () {

        $(document).click(function (e) {
            var container = $("#fh5co-offcanvas, .js-fh5co-nav-toggle");
            if (!container.is(e.target) && container.has(e.target).length === 0) {

                if ($('body').hasClass('offcanvas-visible')) {

                    $('body').removeClass('offcanvas-visible');
                    $('.js-fh5co-nav-toggle').removeClass('active');

                }


            }
        });

    };


    var offcanvasMenu = function () {
        $('body').prepend('<div id="fh5co-offcanvas" />');
        $('#fh5co-offcanvas').prepend('<ul id="fh5co-side-links">');
        $('body').prepend('<a href="#" class="js-fh5co-nav-toggle fh5co-nav-toggle"><i></i></a>');
        $('#fh5co-offcanvas').append($('#fh5co-header nav').clone());
    };


    var burgerMenu = function () {

        $('body').on('click', '.js-fh5co-nav-toggle', function (event) {
            var $this = $(this);

            $('body').toggleClass('fh5co-overflow offcanvas-visible');
            $this.toggleClass('active');
            event.preventDefault();

        });

        $(window).resize(function () {
            if ($('body').hasClass('offcanvas-visible')) {
                $('body').removeClass('offcanvas-visible');
                $('.js-fh5co-nav-toggle').removeClass('active');
            }
        });

        $(window).scroll(function () {
            if ($('body').hasClass('offcanvas-visible')) {
                $('body').removeClass('offcanvas-visible');
                $('.js-fh5co-nav-toggle').removeClass('active');
            }
        });

    };


    var toggleBtnColor = function () {
        if ($('#fh5co-hero').length > 0) {
            $('#fh5co-hero').waypoint(function (direction) {
                if (direction === 'down') {
                    $('.fh5co-nav-toggle').addClass('dark');
                }
            }, {offset: -$('#fh5co-hero').height()});

            $('#fh5co-hero').waypoint(function (direction) {
                if (direction === 'up') {
                    $('.fh5co-nav-toggle').removeClass('dark');
                }
            }, {
                offset: function () {
                    return -$(this.element).height() + 0;
                }
            });
        }
    };


    var contentWayPoint = function () {
        var i = 0;
        $('.animate-box').waypoint(function (direction) {

            if (direction === 'down' && !$(this.element).hasClass('animated-fast')) {

                i++;

                $(this.element).addClass('item-animate');
                setTimeout(function () {

                    $('body .animate-box.item-animate').each(function (k) {
                        var el = $(this);
                        setTimeout(function () {
                            var effect = el.data('animate-effect');
                            if (effect === 'fadeIn') {
                                el.addClass('fadeIn animated-fast');
                            } else if (effect === 'fadeInLeft') {
                                el.addClass('fadeInLeft animated-fast');
                            } else if (effect === 'fadeInRight') {
                                el.addClass('fadeInRight animated-fast');
                            } else {
                                el.addClass('fadeInUp animated-fast');
                            }

                            el.removeClass('item-animate');
                        }, k * 200, 'easeInOutExpo');
                    });

                }, 100);

            }

        }, {offset: '85%'});
    };


    $(function () {
        fullHeight();
        sliderMain();
        centerBlock();
        responseHeight()
        mobileMenuOutsideClick();
        offcanvasMenu();
        burgerMenu();
        toggleBtnColor();
        contentWayPoint();
    });

    $(document).ready(function () {
        $("#search-price-range").slider({
            min: 2,
            max: 140,
            value: [6, 86],
            step: 1,
            focus: true,
            tooltip: 'hide'
        });
        $("#search-price-range").on("slide", function (slideEvt) {
            $("#search-price-from").text(slideEvt.value[0]);
            $("#search-price-to").text(slideEvt.value[1]);
        });
    });

    function get_last_timestamp() {
        return $("#resultsBlock").children().last().children().last().attr("timestamp");
    }

    $('#loadMore').on('click', function () {
        var $btn = $(this).button('loading')
        var url = '/loadMore?timestampUntil=' + get_last_timestamp();
        $.get(url, function (html) {
            $("#resultsBlock").append(html);
            contentWayPoint();

            if (get_last_timestamp() == undefined) {
                $btn.hide();
            } else {
                $btn.button('reset')
            }
        });
    })


    var districtSelect = $('#district-select');
    var roomsButton1 = $('#rooms-btn1');
    var roomsButton2 = $('#rooms-btn2');
    var roomsButton3 = $('#rooms-btn3');
    var searchPriceRange = $('#search-price-range');

    var blurer = function () {
        $(this).blur();
    };
    roomsButton1.onfocus = blurer();
    roomsButton2.onfocus = blurer();
    roomsButton3.onfocus = blurer();

    var searchJsonCreator = function () {
        return {
            "districts[]": districtSelect.val(),
            "rooms1": roomsButton1.attr("aria-pressed"),
            "rooms2": roomsButton2.attr("aria-pressed"),
            "rooms3": roomsButton3.attr("aria-pressed"),
            "priceRange": searchPriceRange.slider('getValue')
        }
    };

    $('#search-button').on('click', function () {
        var searchJson = searchJsonCreator();
        $.post("/search", searchJson, function (html) {

        });
    });

}());