````
	Activity.java

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    
````

````    
    ViewGroup.java
    
    public boolean dispatchTouchEvent(MotionEvent ev) {
    
    
	      // 1.onInterceptTouchEvent触发时机
	     if (actionMasked == MotionEvent.ACTION_DOWN
	                    || mFirstTouchTarget != null) {
	                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
	                if (!disallowIntercept) {
	                    intercepted = onInterceptTouchEvent(ev);//拦截方法
	                }
	    
	    }
	    ....
	    
	    
	    if (!canceled && !intercepted) {
	    
	    	if (newTouchTarget == null && childrenCount != 0) { // 没有newTouchTarget
		    	for (int i = childrenCount - 1; i >= 0; i--) {
		    		if (!isTransformedTouchPointInView(x, y, child, null)) {
						continue;
					}
					newTouchTarget = getTouchTarget(child);
					if (newTouchTarget != null) {
						break;
	             }
	             if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
	             		newTouchTarget = addTouchTarget(child, idBitsToAssign);
	             		break;
	             }
	          }
	       }
	    }
	    
	    
	    if (mFirstTouchTarget == null) {
            // No touch targets so treat this as an ordinary view.
            handled = dispatchTransformedTouchEvent(ev, canceled, null,
                    TouchTarget.ALL_POINTER_IDS);
       } else {
       	  TouchTarget predecessor = null;
            TouchTarget target = mFirstTouchTarget;
            while (target != null) {
                final TouchTarget next = target.next;
                if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
                    handled = true;
                } else {
                    final boolean cancelChild = resetCancelNextUpFlag(target.child)
                            || intercepted;
                    if (dispatchTransformedTouchEvent(ev, cancelChild,
                            target.child, target.pointerIdBits)) {
                        handled = true;
                    }
                    if (cancelChild) {
                        if (predecessor == null) {
                            mFirstTouchTarget = next;
                        } else {
                            predecessor.next = next;
                        }
                        target.recycle();
                        target = next;
                        continue;
                    }
                }
                predecessor = target;
                target = next;
            }
       
       }
	    
	    
	    
    } //END dispatchTouchEvent
    
    
    
    /**
     * @hide
     */
    public void transformPointToViewLocal(float[] point, View child) {
        point[0] += mScrollX - child.mLeft;
        point[1] += mScrollY - child.mTop;

        if (!child.hasIdentityMatrix()) {
            child.getInverseMatrix().mapPoints(point);
        }
    }






 private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
            View child, int desiredPointerIdBits) {
        final boolean handled;

        // Canceling motions is a special case.  We don't need to perform any transformations
        // or filtering.  The important part is the action, not the contents.
        final int oldAction = event.getAction();
        if (cancel || oldAction == MotionEvent.ACTION_CANCEL) { //重置为ACTION_CANCEL事件
            event.setAction(MotionEvent.ACTION_CANCEL);
            if (child == null) {
                handled = super.dispatchTouchEvent(event);
            } else {
                handled = child.dispatchTouchEvent(event);
            }
            event.setAction(oldAction);
            return handled;
        }
        
    

````

````
	View.java
	
	public boolean dispatchTouchEvent(MotionEvent event) {
      
        boolean result = false;      
      
        if (onFilterTouchEventForSecurity(event)) {
            //noinspection SimplifiableIfStatement
            ListenerInfo li = mListenerInfo;
            if (li != null && li.mOnTouchListener != null
                    && (mViewFlags & ENABLED_MASK) == ENABLED
                    && li.mOnTouchListener.onTouch(this, event)) {
                result = true;
            }

            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }

        return result;
    }
	
	
````