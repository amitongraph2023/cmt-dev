import { animate, query, style, transition, trigger } from '@angular/animations';

export const routerTransition = trigger('routerTransition', [
  transition('* <=> *', [
    // Initial state of new route
    query(':enter',
      style({
        height: 'calc(100% - 20px)',
        opacity: 0,
        position: 'fixed',
        width:'calc(100% - 271px)'
      }),
      {optional:true}),
    // move page off screen right on leave
    query(':leave',
      animate('500ms ease',
        style({
          height: 'calc(100% - 20px)',
          opacity: 0,
          position: 'fixed',
          width:'calc(100% - 271px)'
        })
      ),
      {optional:true}),
    // move page in screen from left to right
    query(':enter',
      animate('500ms ease',
        style({
          height: 'calc(100% - 20px)',
          opacity: 1,
          position: 'fixed',
          width:'calc(100% - 271px)'
        })
      ),
      {optional:true}),
  ])
]);
