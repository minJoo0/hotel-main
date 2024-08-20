/**
* Template Name: EstateAgency
* Updated: May 30 2023 with Bootstrap v5.3.0
* Template URL: https://bootstrapmade.com/real-estate-agency-bootstrap-template/
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/
(function() {
  "use strict";

  /**
   * Easy selector helper function
   */
  const select = (el, all = false) => {
    el = el.trim()
    if (all) {
      return [...document.querySelectorAll(el)]
    } else {
      return document.querySelector(el)
    }
  }

  /**
   * Easy event listener function
   */
  const on = (type, el, listener, all = false) => {
    let selectEl = select(el, all)
    if (selectEl) {
      if (all) {
        selectEl.forEach(e => e.addEventListener(type, listener))
      } else {
        selectEl.addEventListener(type, listener)
      }
    }
  }

  /**
   * Easy on scroll event listener 
   */
  const onscroll = (el, listener) => {
    el.addEventListener('scroll', listener)
  }

  /**
   * Toggle .navbar-reduce
   */
  let selectHNavbar = select('.navbar-default')
  if (selectHNavbar) {
    onscroll(document, () => {
      if (window.scrollY > 100) {
        selectHNavbar.classList.add('navbar-reduce')
        selectHNavbar.classList.remove('navbar-trans')
      } else {
        selectHNavbar.classList.remove('navbar-reduce')
        selectHNavbar.classList.add('navbar-trans')
      }
    })
  }

  /**
   * Back to top button
   */
  let backtotop = select('.back-to-top')
  if (backtotop) {
    const toggleBacktotop = () => {
      if (window.scrollY > 100) {
        backtotop.classList.add('active')
      } else {
        backtotop.classList.remove('active')
      }
    }
    window.addEventListener('load', toggleBacktotop)
    onscroll(document, toggleBacktotop)
  }

  /**
   * Preloader
   */
  let preloader = select('#preloader');
  if (preloader) {
    window.addEventListener('load', () => {
      preloader.remove()
    });
  }

  /**
   * Search window open/close
   */
  let body = select('body');
  on('click', '.navbar-toggle-box', function(e) {
    e.preventDefault()
    body.classList.add('box-collapse-open')
    body.classList.remove('box-collapse-closed')
  })

  on('click', '.close-box-collapse', function(e) {
    e.preventDefault()
    body.classList.remove('box-collapse-open')
    body.classList.add('box-collapse-closed')
  })

  /**
   * Initiate Bootstrap validation check
   */
  var needsValidation = document.querySelectorAll('.needs-validation')

  Array.prototype.slice.call(needsValidation)
      .forEach(function(form) {
        form.addEventListener('submit', function(event) {
          if (!form.checkValidity()) {
            event.preventDefault()
            event.stopPropagation()
          }

          form.classList.add('was-validated')
        }, false)
      })

  // /*===============Quantity==============*/
  // var sitePlusMinus = function() {
  //
  //   var value,
  //       quantity = document.getElementsByClassName('quantity-container');
  //
  //   function createBindings(quantityContainer) {
  //     var quantityAmount = quantityContainer.getElementsByClassName('quantity-amount')[0];
  //     var increase = quantityContainer.getElementsByClassName('increase')[0];
  //     var decrease = quantityContainer.getElementsByClassName('decrease')[0];
  //     increase.addEventListener('click', function (e) { increaseValue(e, quantityAmount); });
  //     decrease.addEventListener('click', function (e) { decreaseValue(e, quantityAmount); });
  //   }
  //
  //   function init() {
  //     for (var i = 0; i < quantity.length; i++ ) {
  //       createBindings(quantity[i]);
  //     }
  //   };
  //
  //   function increaseValue(event, quantityAmount) {
  //     value = parseInt(quantityAmount.value, 10);
  //
  //     console.log(quantityAmount, quantityAmount.value);
  //
  //     value = isNaN(value) ? 0 : value;
  //     value++;
  //     quantityAmount.value = value;
  //   }
  //
  //   function decreaseValue(event, quantityAmount) {
  //     value = parseInt(quantityAmount.value, 10);
  //
  //     value = isNaN(value) ? 0 : value;
  //     if (value > 0) value--;
  //
  //     quantityAmount.value = value;
  //   }
  //
  //   init();
  //
  // };
  // sitePlusMinus();
  //
  //

  // JavaScript 코드
    var navLinks = document.querySelectorAll(".hotel-nav .nav-link");
    // 각 링크에 클릭 이벤트 리스너 추가
    navLinks.forEach(function(link) {
      link.addEventListener("click", function(event) {
        // 모든 링크에서 "active" 클래스 제거
        navLinks.forEach(function(link) {
          link.classList.remove("active");
        });

        // 클릭된 링크에 "active" 클래스 추가
        this.classList.add("active");
      });
    });


})();
